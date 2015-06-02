package com.azwave.androidzwave.zwave.driver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.usb.UsbClaimException;
import javax.usb.UsbConfiguration;
import javax.usb.UsbConst;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.usb.UsbNotActiveException;
import javax.usb.UsbNotClaimedException;
import javax.usb.UsbPipe;
import javax.usb.UsbServices;

import com.azwave.androidzwave.zwave.items.Msg;


/**
 * 
 */

/**
 * @author florian
 *
 */
public class UsbDriver implements UsbSerialDriver{
	public UsbDevice myDevice = null; 
	public UsbHub hub = null;
	
	/* Interface for usb device & user interaction */
	private UsbInterface iface = null;
	
	/* EndPoint of the usb device */
	private UsbEndpoint readEndPoint = null;
	private UsbEndpoint writeEndPoint = null;
	
    private static final int USB_WRITE_TIMEOUT_MILLIS = 5000;

    /* Write & Read buffer/byte to send data to the usb device */
    public static final int DEFAULT_READ_BUFFER_SIZE = 16 * 1024;
    public static final int DEFAULT_WRITE_BUFFER_SIZE = 16 * 1024;
	private Object readBufferLock = new Object();	
	private Object writeBufferLock = new Object();	
    private byte[] readBuffer;
    private byte[] writeBuffer;
    
	/* The vendor ID */
    private static final short VENDOR_ID = 0x10c4;

    /* The vendor ID */
    private static final short PRODUCT_ID = (short)0xea60;
    
    public UsbDriver(){

    	readBuffer = new byte[DEFAULT_READ_BUFFER_SIZE];
    	writeBuffer = new byte[DEFAULT_WRITE_BUFFER_SIZE];
    	
    	UsbServices services = null;
		try {
			services = UsbHostManager.getUsbServices();
			this.hub = services.getRootUsbHub();
		} catch (SecurityException | UsbException e) {
			e.printStackTrace();
		}
		initUsb();
    }
    
    /** 
     * Find the right usb device
     * @param hub
     * @param vendorId of the device
     * @param productId of the device
     * @return
     */
    public UsbDevice findDevice(UsbHub hub, short vendorId, short productId)
    {
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices())
        {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            if (desc.idVendor() == vendorId && desc.idProduct() == productId) return device;
            if (device.isUsbHub()){
                device = findDevice((UsbHub) device, vendorId, productId);
                if (device != null) 
                	return device;
            }
        }
        return null;
    }
	
    // Initialise the usb device
	public void initUsb(){
		try {
			myDevice = findDevice(hub, VENDOR_ID, PRODUCT_ID);
			if(myDevice != null)
				System.out.println("Device found: " + myDevice.getProductString());
			getEndpoints();
		} catch(UnsupportedEncodingException | UsbException e ){
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the read endpoint and the write endpoint of the usb device
	 * @throws UsbClaimException
	 * @throws UsbNotActiveException
	 * @throws UsbDisconnectedException
	 * @throws UsbException
	 */
	public void getEndpoints() throws UsbClaimException, UsbNotActiveException, UsbDisconnectedException, UsbException{
		UsbConfiguration configuration = myDevice.getActiveUsbConfiguration();
		UsbInterface iface = configuration.getUsbInterface((byte) 0);
		iface.claim();
		for(UsbEndpoint ep: (List<UsbEndpoint>) iface.getUsbEndpoints()){
			if(ep.getType() == UsbConst.ENDPOINT_TYPE_BULK){
				if(ep.getDirection() == UsbConst.ENDPOINT_DIRECTION_IN){
					readEndPoint = ep;
					System.out.println("readEndPoint initialized");
				}
				else if(ep.getDirection() == UsbConst.ENDPOINT_DIRECTION_OUT){
					writeEndPoint = ep;
					System.out.println("writeEndPoint initialized");
				}
			}
		}
		iface.release();
	}
	
	public void claimInterface(){
		UsbConfiguration configuration = myDevice.getActiveUsbConfiguration();
		iface = configuration.getUsbInterface((byte) 0);
		if(!iface.isClaimed()){
			try {
				iface.claim();
				System.out.println("Interface claimed");
			} catch (UsbNotActiveException | UsbDisconnectedException | UsbException e) {
				System.out.println("Interface not claimed");
				e.printStackTrace();
			}
		} else {
			System.out.println("Interface already claimed");
		}
	}
	
	public void releaseInterface(){
		try {
			iface.release();
			System.out.println("Interface released");
		} catch (UsbNotActiveException | UsbDisconnectedException | UsbException e) {
			System.out.println("Interface not released");
			e.printStackTrace();
		}
	}
	
	@Override
	public int read(byte[] dest) throws UsbNotActiveException, UsbNotClaimedException, UsbDisconnectedException, UsbException{
		claimInterface();
		UsbPipe pipe = readEndPoint.getUsbPipe();
		pipe.open();
		
		final int numBytesRead;
        synchronized (readBufferLock) {
            numBytesRead = pipe.syncSubmit(dest);
            if (numBytesRead < 0) {
                return 0;
            }
            System.arraycopy(readBuffer, 0, dest, 0, numBytesRead);
        }
        releaseInterface();
        return numBytesRead;
	}

	@Override
	public int write(byte[] src) throws UsbNotActiveException, UsbNotClaimedException, UsbDisconnectedException, UsbException{
		// Claim Interface & open the pipe for communication
		claimInterface();
		UsbPipe pipe = writeEndPoint.getUsbPipe();
		pipe.open();
		System.out.println("Pipe open");
		
		int offset = 0;

        while (offset < src.length) {
            final int writeLength, amtWritten;

            synchronized (writeBufferLock) {
                final byte[] writeBufferTMP;

                writeLength = Math.min(src.length - offset, writeBuffer.length);
                if (offset == 0) {
                	writeBufferTMP = src;
                } else {
                    // bulkTransfer does not support offsets, make a copy.
                    System.arraycopy(src, offset, writeBuffer, 0, writeLength);
                    writeBufferTMP = writeBuffer;
                }

                amtWritten = pipe.syncSubmit(writeBufferTMP);
//                amtWritten = pipe.syncSubmit(src);
            }
            if (amtWritten <= 0) {
                try {
					throw new IOException("Error writing " + writeLength  + " bytes at offset " + offset + " length=" + src.length);
				} catch (IOException e) {
					e.printStackTrace();
				}
            }

            System.out.println("Wrote amt=" + amtWritten + " attempted=" + writeLength);
            offset += amtWritten;
        }
        pipe.close();
        releaseInterface();
        return offset;
	}
	

	
	@Override
	public boolean getCD() throws IOException {
        return false;
    }
	
	@Override
	public boolean getCTS() throws IOException {
        return false;
    }

    @Override
    public boolean getDSR() throws IOException {
        return false;
    }

    @Override
    public boolean getDTR() throws IOException {
        return true;
    }

    @Override
    public void setDTR(boolean value) throws IOException {
    }

    @Override
    public boolean getRI() throws IOException {
        return false;
    }

    @Override
    public boolean getRTS() throws IOException {
        return true;
    }

    @Override
    public void setRTS(boolean value) throws IOException {
    }

    public static Map<Integer, int[]> getSupportedDevices() {
        final Map<Integer, int[]> supportedDevices = new LinkedHashMap<Integer, int[]>();
        supportedDevices.put(Integer.valueOf(UsbId.VENDOR_SILAB), new int[] { UsbId.SILAB_CP2102 });
        return supportedDevices;
    }
}

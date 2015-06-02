Partie 1 du PJI: créer un driver java afin de pouvoir utiliser les objets Zwave depuis un ordinateur en utilisant une clé usb Zwave.


	Les parties de code concernant android sont soit placées en commentaire, soit remplacées par du code java.



Modifications
-------------

	Création du fichier UbsDriver.java implémentant les fonctions read() et write() du driver.
Ce driver n'est pas fonctionnel, une exception est déclenchée lors de l'écriture ou de la lecture. (La librairie usb-4-java est utilisée, les dossiers contenues dans le dossier lib doivent donc être ajoutés dans la compilation du projet).


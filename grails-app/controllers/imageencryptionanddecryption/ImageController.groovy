package imageencryptionanddecryption

import encryption.AES
import encryption.Constants
import encryption.Encryption
import encryption.EncryptionController
import grails.util.Holders
import org.apache.commons.io.FilenameUtils

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

class ImageController {

    def index() {}

    def save() {
        savePhotoToDisk(request.getFile('photo'))
        print("Hereee----")


    }

    def savePhotoToDisk(def f)

    {
        println("Now heree----")
        Constants constants = new Constants();
        constants.PHOTOS_DIR = Holders.getGrailsApplication().getMainContext().getResource("/").getFile().getAbsolutePath();

        def okContentTypes = ['image/png', 'image/jpeg', 'image/jpg']
        if (f.empty) {
            print("not empty")
            flash.message = 'file cannot be empty'
            return
        }
        if (!okContentTypes.contains(f.contentType)) {
            print("type")

            flash.message = "Image must be one of: $okContentTypes"
            return
        }
        print("1------")
        def extension = FilenameUtils.getExtension(f.originalFilename)

        def serverImagesDir = new File(encryption.Constants.PHOTOS_DIR)
        print(serverImagesDir);
        Encryption encryption = new Encryption();
        AES aes = new AES();
        EncryptionController encryptionController = new EncryptionController();
        println("2----------")
        if (serverImagesDir.exists()) {
            println("photoooo---")
            def fileName = "photo" + ".$extension"
            File destinationFile = new File(serverImagesDir, fileName)

            //  encryption.photoUrl = fileName

            f.transferTo(destinationFile)
            println("convert")

            // read "any" type of image (in this case a png file)
            byte[] k=new byte[16];
            try
            {
                k=AES.keygeneration();
            }
            catch (Exception e){
                println "Problem in keyGeneration!!!!!!!!";
            }

            println "key == "+k
//            BufferedImage image = ImageIO.read(new File("C:\\Java_Projects\\AESAlgorithm\\web-app\\images\\springsource.PNG"));
            BufferedImage image = ImageIO.read(new File("C:\\Java_Projects\\AES\\cameraman.jpg"));
            String path = "C:\\Users\\Sushant\\Desktop\\"
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            byte[] b = baos.toByteArray();
            byte[] b2 = new byte[b.length-620];
            byte[] b1 = new byte[0];
            for(int i=0; i<(b2.length); i++)
                b2[i]=b[i+620];

            b2=AES.encrypt(b2,k,10)
            b1=new byte[b2.length+620];
            for(int i=0; i<b1.length; i++) {
                if(i<620) b1[i]=b[i];
                else b1[i]=b2[i-620]; }
            File inputFile
            inputFile=new File(path+"encryptedimage.jpg");
            FileOutputStream fos = new FileOutputStream(inputFile);
            fos.write(b1);
            fos.flush();
            fos.close();

            for(int i=0; i<b2.length; i++)
                b2[i]=b1[i+620];

            b2=AES.decrypt(b2,k,10)
            for(int i=0; i<b.length; i++) {
                if(i<620) b[i]=b1[i];
                else b[i]=b2[i-620]; }

            for(int i=0; i<b.length; i++) {
                if(i<620) b[i]=b1[i];
                else b[i]=b2[i-620]; }
            inputFile=new File(path+"decryptedimage.jpg");
            fos = new FileOutputStream(inputFile);
            fos.write(b);
            fos.flush();
            fos.close();





        }
    }
}
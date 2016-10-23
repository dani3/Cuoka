package es.sidelab.cuokawebscraperrestclient.test;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class mainHyM 
{
    static boolean finished = false;
    public static void main(String[] args) throws Exception 
    {        
        
        String url = "http://www2.hm.com/es_es/";
        Section section = new Section("Camisas", "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\HyM_true\\false\\", false);
        //Section section = new Section("Vestidos", "C:\\Users\\Dani\\Documents\\shops\\Blanco_true\\false\\", false);
        
        // Ejecutamos el script que crea el fichero con todos los productos.
        Process process = Runtime.getRuntime().exec(new String[] {"python"
                                , section.getPath() + "renderProducts.py"
                                , Properties.CHROME_DRIVER
                                , section.getName()
                                , section.getPath()});
        
        // Nos quedamos esperando hasta que termine.
        File file = new File(section.getPath() + section.getName() + "_done.dat");
        while (!file.exists()) 
        {
            file = new File(section.getPath() + section.getName() + "_done.dat");
        }

        file.delete();
        
        // Una vez ha terminado de generar el fichero de productos, lo leemos.
        BufferedReader br = new BufferedReader(
            new FileReader(new File(section.getPath() + section.getName() + "_products.txt")));
               
      
        List<Product> productList = new ArrayList<>();
        Product product;
        br.readLine();
        while(!finished) // linea de comienzo de producto ---
        {   
           //empezamos nuevo producto
            product = _readProductGeneralInfo(br);
            if (product != null) //todo ha ido bien, seguimos leyendo los colores
            {
                product = _readProductColors(product, br);
                if ((product != null)) //&& (!containsProduct(productList, product.getColors().get(0).getReference()))) // todo ha ido bien, añadimos a la lista
                {
                    productList.add(product);                                            
                }
            }
        }        

        /*********************************************************************/

        System.out.println("Total: " + productList.size());
        
        for (Product p: productList) 
        {
            System.out.println("-------- INFO PRODUCTO ----------");
            System.out.println("Nombre: " + p.getName());
            System.out.println("Link: " + p.getLink());
            System.out.println("Descripcion: " + p.getDescription());
            System.out.println("Precio: " + p.getPrice() + " €");
            System.out.println("-------- INFO COLORES -----------");
            for (ColorVariant cv : p.getColors())
            {
                System.out.println(" - Color: " + cv.getName());
                System.out.println(cv.getColorURL());
                System.out.println(" - Referencia: " + cv.getReference());
                for (Image image : cv.getImages())
                    System.out.println(" - " + image.getUrl());
                System.out.println("\n");            
            }
        }
    }
    
    private static Product _readProductGeneralInfo(BufferedReader br) throws IOException
    {
        String name = br.readLine();
        String description = br.readLine();
        String price = br.readLine();
        String link = br.readLine();
        
        // Podemos haber leido ya todos los productos, por lo que name puede ser null
        if (name == null || name.contains("null") || price.contains("null"))
        {
            return null;
        }
        
        Product product = new Product();
        product.setName(name.replace("Nombre: ", ""));
        product.setDescription(description.replace("Descripcion: ", ""));
        product.setPrice(Double.valueOf(price.replace("Precio: ", "")));
        product.setLink(fixURL(link.replace("Link: ", "")));
        
        return product;        
    }
    
    private static Product _readProductColors(Product product, BufferedReader br) throws IOException
    {
        
        List<ColorVariant> colors = new ArrayList<>();
        boolean doneColor = false;
        br.readLine();   //leemos los *********
        while (!doneColor)
        {
            boolean correct = true;
            ColorVariant color = new ColorVariant();
            List<Image> images = new ArrayList<>();
            
            String colorName = br.readLine();
            String colorIcon = br.readLine();
            String reference = br.readLine();
            if(colorName.contains("null") || reference.contains("null"))
            {
                correct = false;
                String line = br.readLine();
                if(!line.contains("****")){
                    doneColor = true;
                }                        
            }
            if (correct){
                color.setName(colorName.replace("  Color: ", ""));
                color.setColorURL(fixURL(colorIcon.replace("  Icono: ", ""))); 
                color.setReference(reference.replace("  Referencia: ", ""));

                /*imagenes*/
                boolean doneImages = false;
                while (!doneImages)
                {
                    String url = br.readLine();
                    if (url == null){
                        doneImages = true;
                        doneColor = true;
                        finished = true;
                    }
                    else if (url.contains("***")){
                        /*hemos acabado con las imágenes pero no con los colores*/
                        doneImages = true; 
                    } 
                    else if (url.contains("------") || url.length() == 0) //producto final ==0
                    {
                        doneImages = true;
                        doneColor = true;
                    }
                    else {
                        Image image = new Image(fixURL(url.replace("     Imagen: ", "")));
                        images.add(image);
                    }

                }
                color.setImages(images);
                colors.add(color);
            }
        }
        product.setColors(colors);
        return product;
    }   
    
    private static boolean containsProduct(List<Product> productList, String reference)
    {
        for (Product p : productList)
            for (ColorVariant cv : p.getColors())
                if (cv.getReference().equals(reference))
                    return true;
        
        return false;
    }
    
    private static String fixURL(String url)
    {
        if (url.startsWith("//"))
            return "http:".concat(url).replace(" " , "%20");
        
        return url.replace(" " , "%20");
    }   
}

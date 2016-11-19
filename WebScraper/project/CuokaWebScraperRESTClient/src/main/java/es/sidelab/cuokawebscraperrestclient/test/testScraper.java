package es.sidelab.cuokawebscraperrestclient.test;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class testScraper 
{
    private static boolean finished = false;
    
    public static void main(String[] args) throws Exception 
    {              
        List<Product> productList = new ArrayList<>();
        
        //Section section = new Section("Camisetas", "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\Blanco_true\\false\\", false);
        Section section = new Section("Vestidos", "C:\\Users\\Dani\\Documents\\shops\\Blanco_true\\false\\", false);
        
        // Ejecutamos el script que crea el fichero con todos los productos.
        Runtime.getRuntime().exec(new String[] {"python"
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
        
        br.readLine();
        while(!finished)
        {   
           // Empezamos nuevo producto
            Product product = _readProductGeneralInfo(br);
            if (product != null)
            {
                product = _readProductColors(product, br);
                if ((product != null) && (!containsProduct(productList, product.getColors().get(0).getReference()))) 
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
        
        // Leemos los asteriscos
        br.readLine();     
        while (!doneColor)
        {
            List<Image> images = new ArrayList<>();
            ColorVariant color = new ColorVariant();
            
            String colorName = br.readLine();
            String colorIcon = br.readLine();
            String reference = br.readLine();
            if(colorName.contains("null") || reference.contains("null"))
            {
                return null;
            }
            
            color.setName(colorName.replace("  Color: ", ""));
            color.setColorURL(colorIcon); 
            color.setReference(reference.replace("  Referencia: ", ""));
            
            // Leemos las imagenes
            boolean doneImages = false;
            while (!doneImages)
            {
                String url = br.readLine();
                if (url == null)
                {
                    // Si la url es null, es que hemos llegado al final del fichero.
                    doneImages = true;
                    doneColor  = true;
                    finished   = true;
                    
                } else if (url.contains("***")) {
                    // Hemos acabado con las imágenes pero no con los colores
                    doneImages = true; 
                    
                } else if (url.contains("------") || url.length() == 0) {
                    // Producto final == 0
                    doneImages = true;
                    doneColor = true;
                    
                } else {
                    Image image = new Image(fixURL(url.replace("     Imagen: ", "")));
                    images.add(image);
                }
            }
            
            color.setImages(images);
            colors.add(color);            
        }
        
        // Nos aseguramos de que no insertamos productos sin ningun color.
        if (colors.isEmpty()) 
        {
            return null;
        }
        
        product.setColors(colors);
        
        return product;
    }   
    
    private static boolean containsProduct(List<Product> productList, String reference)
    {
        for (Product p : productList)
        {
            for (ColorVariant cv : p.getColors())
            {
                if (cv.getReference().equals(reference))
                {
                     return true;
                }
            }
        }
        
        return false;
    }
    
    private static String fixURL(String url)
    {
        if (url.startsWith("//"))
        {
            return "http:".concat(url).replace(" " , "%20");
        }
        
        return url.replace(" " , "%20");
    }   
}

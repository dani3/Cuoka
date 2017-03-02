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
import javax.annotation.Nullable;
import org.springframework.util.StringUtils;

public class testScraper 
{
    private static boolean finished = false;
    
    public static void main(String[] args) throws Exception 
    {              
        List<Product> productList = new ArrayList<>();
        
        /***************** HyM *****************/
        //Section section = new Section("Camisetas", "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\HyM_true\\false\\", false);
        //Section section = new Section("Camisas", "C:\\Users\\Dani\\Documents\\shops\\HyM_true\\false\\", false);
        
        /***************** Pedro Del Hierro *****************/
        //Section section = new Section("Camisetas", "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\Pedro Del Hierro_true\\false\\", false);
        //Section section = new Section("Abrigos", "C:\\Users\\Dani\\Documents\\shops\\Pedro Del Hierro_true\\true\\", false);
        
        /***************** Bershka *****************/
        //Section section = new Section("Camisetas", "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\Bershka_true\\false\\", false);
        Section section = new Section("Scraping_validation", "C:\\Users\\Dani\\Documents\\shops\\Bershka_false\\false\\", false);
        
        /***************** Zara *****************/
        //Section section = new Section("Camisetas", "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\Bershka_true\\false\\", false);
        //Section section = new Section("Camisetas", "C:\\Users\\Dani\\Documents\\shops\\Zara_true\\false\\", false);
        
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
        while (!finished)
        {   
            // Empezamos nuevo producto
            Product product = _readProductGeneralInfo(br);
            if (product != null)
            {
                product = _readProductColors(product, br);
                if ((product != null) && (!_containsProduct(productList, product.getColors().get(0).getReference()))) 
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
            System.out.println("Descuento: " + p.getDiscount()+ " €");
            System.out.println("-------- INFO COLORES -----------");
            for (ColorVariant cv : p.getColors())
            {
                System.out.println(" - Color: " + cv.getName());
                System.out.println(" - Icono: " + cv.getColorURL());
                System.out.println(" - Referencia: " + cv.getReference());
                for (Image image : cv.getImages())
                {
                    System.out.println(" - " + image.getUrl());
                }
                
                System.out.println("\n");     
            }
        }
    }
    
    /**
     * Metodo que lee los atributos básicos del producto.
     * @param br: BuffereReader
     * @return producto.
     * @throws IOException 
     */
    @Nullable
    private static Product _readProductGeneralInfo(BufferedReader br) throws IOException
    {
        Product product = new Product();
        
        String name;
        String description;
        String price;
        String discount;
        String link;
        
        // Leemos el nombre.
        name = br.readLine();        
        // Podemos haber leido ya todos los productos, por lo que name puede ser null
        // o podemos leer la linea de guiones si la conexion ha fallado.
        if (name == null || name.contains("null") || name.contains("---------"))
        {
            if ((name != null) && name.contains("null"))
            {                
                // Leemos la linea de guiones
                br.readLine();
            }
            
            if ((name != null) && name.contains("---------"))
            {
                // Leemos la linea de guiones
                br.readLine();
            }
            
            // Si leemos el EOF marcamos finished a true.
            if (name == null)
            {
                finished = true;
            }
            
            return null;
        }
        
        // Leemos la descripcion y el precio.
        description = br.readLine();
        price       = br.readLine();      
        
        if (price.contains("null"))
        {
            if (price.contains("null"))
            {
                br.readLine();
            }
            
            return null;
        }
        
        // Leemos el descuento y el link.
        discount = br.readLine();
        link     = br.readLine();
        
        // Eliminamos las cabeceras.
        name        = name.replace("Nombre: ", "");
        description = description.replace("Descripcion: ", "");
        discount    = discount.replace("Descuento: ", "").trim();   
        price       = price.replace("Precio: ", "").trim();
        link        = link.replace("Link: ", "");
        
        // Eliminamos el primer '.' en caso de que el precio supere los 1000 euros.
        if (StringUtils.countOccurrencesOf(price, ".") > 1)
        {
            price = price.replace(".", "");
        }
        
        // Lo mismo con el descuento.
        if (StringUtils.countOccurrencesOf(discount, ".") > 1)
        {
            discount = discount.replace(".", "");
        }
        
        double _price;
        double _discount;
        
        try
        {
            _price = Double.valueOf(price);
            
        } catch (Exception e) {
            return null;
        }
        
        try
        {
            _discount = (discount.isEmpty()) ? 0.0f : Double.valueOf(discount);
            
        } catch (Exception e) {
            _discount = 0.0f;
        }
        
        product.setName(name);
        product.setDescription(description);
        
        if (_discount > 0.0f)
        {
            product.setPrice(Math.min(_price, _discount));            
            product.setDiscount(Math.max(_price, _discount));
        } else {
            product.setPrice(_price);            
            product.setDiscount(_discount);
        }
        
        product.setLink(fixURL(link));
        
        return product;            
    }
    
    /**
     * Metodo que lee los colores y los inserta en el producto.
     * @param product: producto al que insertar los colores.
     * @param br: BufferedReader.
     * @return producto.
     * @throws IOException 
     */
    @Nullable
    private static Product _readProductColors(Product product, BufferedReader br) throws IOException
    {        
        List<ColorVariant> colors = new ArrayList<>();
        boolean doneColor = false;
        
        // Leemos los asteriscos
        br.readLine();     
        while (!doneColor)
        {
            boolean correct = true;
            
            List<Image> images = new ArrayList<>();
            ColorVariant color = new ColorVariant();
            
            String colorName = br.readLine();
            String colorIcon = br.readLine();
            String reference = br.readLine();
            if (colorName.contains("null") || reference.contains("null"))
            {
                correct = false;
                
                String line = br.readLine();                
                // Podemos llegar al final de fichero.
                if (line == null)
                {
                    finished = true;
                    break;
                }
                
                // O podemos llegar al siguiente producto.
                if (!line.contains("******"))
                {
                    doneColor = true;
                }
            }
            
            if (correct)
            {
                color.setName(colorName.replace("  Color: ", ""));
                color.setColorURL(colorIcon.replace("  Icono: ", ""));
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
                        doneColor  = true;

                    } else {
                        if (!url.replace("     Imagen: ", "").isEmpty() && !url.contains("null"))
                        {
                            Image image = new Image(fixURL(url.replace("     Imagen: ", "")));
                            images.add(image);
                        }  
                    }
                }

                color.setImages(images);
                colors.add(color); 
            }
        }
        
        // Nos aseguramos de que no insertamos productos sin ningun color.
        if (colors.isEmpty()) 
        {
            return null;
        }
        
        product.setColors(colors);
        
        return product;
    }   
    
    /**
     * Metodo que comprueba si el producto esta ya en la lista.
     * @param productList: lista de productos.
     * @param reference: producto a buscar.
     * @return true si el producto ya se encuentra en la lista.
     */
    private static boolean _containsProduct(List<Product> productList, String reference)
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
    
    /**
     * Metodo que corrige una url si es incorrecta. Codifica los espacios y añade la cabecera HTTP.
     * @param url: url a corregir.
     * @return url corregida.
     */
    private static String fixURL(String url)
    {
        if (url.startsWith("//"))
        {
            return "http:".concat(url).replace(" " , "%20");
        }
        
        return url.replace(" " , "%20");
    }  
}

package es.sidelab.cuokawebscraperrestclient.test;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import es.sidelab.cuokawebscraperrestclient.utils.Printer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class mainBlanco 
{
    public static void main( String[] args ) throws Exception 
    {        
        String url = "https://www.blanco.com/";
        Section section = new Section( "Camisas", "C:\\Users\\Dani\\Documents\\shops\\Blanco_true\\false\\", false );

        // Ejecutamos el script que crea el fichero con todos los productos.
        Process p = Runtime.getRuntime().exec( new String[] { "python"
                                , section.getPath() + "renderProducts.py"
                                , Properties.CHROME_DRIVER
                                , section.getName()
                                , section.getPath() } );
        
        // Nos quedamos suspendidos 10 segundos.
        Thread.sleep( 10000 );
        
        // Nos quedamos esperando hasta que termine.
        File file = new File( section.getPath() + section.getName() + "_done.dat" );
        while ( ! file.exists() ) 
        {
            file = new File( section.getPath() + "done.dat" );
        }

        file.delete();
        
        // Una vez ha terminado de generar el fichero de productos, lo leemos.
        BufferedReader br = new BufferedReader( 
            new FileReader( new File( section.getPath() + section.getName() + "_products.txt" ) ) );
        
        String name;
        String description;
        double price;
        String reference;
        String link;
        List<ColorVariant> colors = new ArrayList<>();
        List<Image> images = new ArrayList<>();
        Product product = null;
        ColorVariant color = null;
        
        // Ignoramos la primera linea.
        String line = br.readLine();
        boolean done = false;
        while(!done)
        {
            product = _readProductGeneralInfo(br);
            br.readLine();
            while
                String _readProductColors(product, br);
        }
        
        /*********************************************************************/
        Printer.print( Integer.toString( productList.size() ) );
        
        Product p = productList.get( 0 );
        
        System.out.println( "-------- INFO PRODUCTO ----------" );
        System.out.println( "Nombre: " + p.getName() );
        System.out.println( "Link: " + p.getLink() );
        System.out.println( "Descripcion: " + p.getDescription());
        System.out.println( "Precio: " + p.getPrice() + " €" );
        System.out.println( "-------- INFO COLORES -----------" );
        for ( ColorVariant cv : p.getColors() )
        {
            System.out.println( " - Color: " + cv.getName() );
            System.out.println( " - Icono: " + cv.getColorURL() );
            System.out.println( " - Referencia: " + cv.getReference() );
            for ( Image image : cv.getImages() )
                System.out.println( " - " + image.getUrl() );
          
            
            System.out.println( "\n" );            
        }
    }
    
    private static Product _readProductGeneralInfo(BufferedReader br) throws IOException
    {
        String name = br.readLine();
        String description = br.readLine();
        String price = br.readLine();
        String link = br.readLine();
        
        if (name.contains("null") || price.contains("null"))
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
        //ignoramos la primera línea - lista de colores
        //br.readLine();
        boolean doneColor = false;
        while (!doneColor)
        {
            ColorVariant color = new ColorVariant();
            List<Image> images = new ArrayList<>();
            
            String colorName = br.readLine();
            String colorIcon = br.readLine();
            String reference = br.readLine();
            if(colorName.contains("null") || reference.contains("null"))
            {
                return null;
            }
            color.setName(colorName.replace("Color: ", ""));
            color.setColorURL(fixURL(colorIcon.replace("Icono: ", "")));
            color.setReference(reference.replace("Referencia: ", ""));
            
            /*imagenes*/
            boolean doneImages = false;
            while (!doneImages)
            {
                String url = br.readLine();
                if url.contains("Color: "){
                    _readProductColors();
                    break;
                } 
                if (url.contains("------"))
                {
                    doneImages = true;
                    doneColor = true;
                }
                else{
                    Image image = new Image(fixURL(url.replace("Imagen: ", "")));
                    images.add(image);
                }
            }
        }
        
    }
    
    private static String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url.replace( " " , "%20" );
    }   
    
    private static int containsProduct( List<Product> productList, String reference )
    {
        for ( int i = 0; i < productList.size(); i++ )
            for ( ColorVariant cv : productList.get( i ).getColors() )
                if ( cv.getReference().equals( reference ) )
                    return i;
        
        return -1;
    }
}

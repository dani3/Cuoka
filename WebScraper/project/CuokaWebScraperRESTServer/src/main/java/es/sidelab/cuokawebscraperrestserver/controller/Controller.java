package es.sidelab.cuokawebscraperrestserver.controller;

import es.sidelab.cuokawebscraperrestserver.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestserver.beans.Feedback;
import es.sidelab.cuokawebscraperrestserver.beans.Filter;
import es.sidelab.cuokawebscraperrestserver.beans.Product;
import es.sidelab.cuokawebscraperrestserver.beans.Shop;
import es.sidelab.cuokawebscraperrestserver.beans.ShopSuggested;
import es.sidelab.cuokawebscraperrestserver.beans.User;
import es.sidelab.cuokawebscraperrestserver.beans.UserModification;
import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import es.sidelab.cuokawebscraperrestserver.repositories.FeedbackRepository;
import es.sidelab.cuokawebscraperrestserver.repositories.ProductsRepository;
import es.sidelab.cuokawebscraperrestserver.repositories.ShopSuggestedRepository;
import es.sidelab.cuokawebscraperrestserver.repositories.ShopsRepository;
import es.sidelab.cuokawebscraperrestserver.repositories.UsersRepository;
import es.sidelab.cuokawebscraperrestserver.utils.ColorManager;
import es.sidelab.cuokawebscraperrestserver.utils.ImageManager;
import es.sidelab.cuokawebscraperrestserver.utils.SectionManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador que proporcionara las URLs a los scrapers y tambien guardara los productos.
 * @author Daniel Mancebo Aldea
 */

@RestController
public class Controller 
{
    private static final Log LOG = LogFactory.getLog(Controller.class);
    
    @Autowired
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(1);
    
    @Autowired
    private ColorManager colorManager;
    
    @Autowired
    private SectionManager sectionManager;
    
    @Autowired
    private ProductsRepository productsRepository;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private ShopsRepository shopsRepository;
    
    @Autowired
    private ShopSuggestedRepository shopSuggestedRepository;
    
    @Autowired
    private FeedbackRepository feedbackRepository;
    
    /**
     * Metodo que anade la nueva tienda sugerida por un usuario.
     * @param shopSuggested: tienda sugerida por un usuario.
     * @return true si ha ido correctamente.
     */
    @RequestMapping(value = "/suggested", method = RequestMethod.POST)
    public String addShopSuggested(@RequestBody ShopSuggested shopSuggested)
    {
        shopSuggestedRepository.save(shopSuggested);
        
        return Properties.ACCEPTED;
    }
    
    /**
     * Metodo que anade la opinion de un usuario.
     * @param feedback: opinion del usuario.
     * @return true si ha ido correctamente.
     */
    @RequestMapping(value = "/feedback", method = RequestMethod.POST)
    public String addFeedback(@RequestBody Feedback feedback)
    {
        feedbackRepository.save(feedback);
        
        return Properties.ACCEPTED;
    }
    
    /**
     * Metodo que anade un usuario a la BD.
     * @param user: usuario a anadir.
     * @return String con el resultado de la operacion.
     */
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String registerUser(@RequestBody User user)
    {     
        LOG.info("[LOGIN] Peticion POST para anadir un nuevo usuario");
        LOG.info("[LOGIN] Comprobando que no exista...");
        
        // Comprobamos que no existe el usuario. Si existe, se devuelve 'USER_ALREADY_EXISTS'
        if (usersRepository.findByEmail(user.getEmail()) != null)
        {
            LOG.warn("[LOGIN] El usuario con email (" + user.getEmail() + ") ya existe");
        
            return Properties.ALREADY_EXISTS;
        }
        
        // Asignamos la fecha de registro.
        LOG.info("[LOGIN] El usuario no existe, se registra con fecha de: " + Calendar.getInstance());
        user.setRegistrationDate(Calendar.getInstance());
        
        // Guardamos el usuario en BD.
        usersRepository.save(user);
        
        // Sacamos el ID que se le ha asignado al guardarlo, y se devuelve.
        long id = usersRepository.findByEmail(user.getEmail()).getId();        
        LOG.info("[LOGIN] Usuario guardado correctamente (ID: " + id + ")");
        
        return String.valueOf(id);
    }
    
    /**
     * Metodo que elimina un usuario.
     * @param id: id del usuario.
     * @return true si ha ido correctamente.
     */
    @RequestMapping(value = "/users/{id}" , method = RequestMethod.DELETE)
    public String deleteUser(@PathVariable long id)
    {
        LOG.info("[DELETE] Peticion DELETE para borrar un usuario (ID: " + id + ")");
        usersRepository.delete(id);
        
        LOG.info("[DELETE] Usuario borrado correctamente");
        return Properties.ACCEPTED;
    }
    
    /**
     * Metodo que recibe las modificaciones de un usuario.
     * @param userModification: objeto con las modificaciones.
     * @param id: id del usuario.
     * @return true si ha ido correctemante.
     */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.POST)
    public String updateUser(@RequestBody UserModification userModification
                    , @PathVariable long id)
    {
        LOG.info("[UPDATE] Peticion POST para modificar un usuario (ID: " + id + ")");
        
        User user = usersRepository.findOne(id);
        if (user == null)
        {
            LOG.warn("[UPDATE] Usuario con ID (" + id + ") no encontrado");
            return Properties.USER_NOT_FOUND;
            
        } else {
            LOG.info("[UPDATE] Usuario con ID (" + id + ") encontrado, se realizan los siguientes cambios.");
            
            String name = userModification.getName();
            String email = userModification.getEmail();
            String password = userModification.getPassword();
            short age = userModification.getAge();
            int postalCode = userModification.getPostalCode();
            
            if ((name != null) && (!name.isEmpty()))
            {
                LOG.info("[UPDATE]  -  Nombre: " + name);
                user.setName(name);
            }
            
            if ((email != null) && (!email.isEmpty()))
            {
                LOG.info("[UPDATE]  -  Email: " + email);
                user.setEmail(email);
            }
            
            if ((password != null) && (!password.isEmpty()))
            {
                LOG.info("[UPDATE]  -  Contrasena: " + password);
                user.setPassword(password);
            }
            
            if (age > 0)
            {
                LOG.info("[UPDATE]  -  Edad: " + age);
                user.setAge(age);
            }
            
            if (postalCode > 0)
            {
                LOG.info("[UPDATE]  -  Codigo postal: " + postalCode);
                user.setPostalCode(postalCode);
            }
            
            usersRepository.save(user);
            
            LOG.info("[UPDATE] Usuario con ID (" + id + ") modificado correctamente");
            
            return Properties.ACCEPTED;
        }        
    }
    
    /**
     * Metodo que devuelve los datos de un usuario.
     * @param id: id del usuario.
     * @return usuario.
     */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public User getUserInfo(@PathVariable long id)
    {
        LOG.info("[LOGIN] Peticion GET para obtener los datos del usuario (ID: " + id + ")");
        
        // Buscamos el usuario con el ID.
        User user = usersRepository.findOne(id);
        
        if (user == null)
        {
            LOG.warn("[LOGIN] Usuario con ID (" + id + ") no encontrado");
        } else {
            LOG.info("[LOGIN] Usuario con ID (" + id + ") encontrado");
        }
        
        // Si no se encuentra el usuario, se devuelve null.
        return user;
    }
    
    /**
     * Metodo que loguea un usuario.
     * @param email: email del usuario.
     * @param password: contraseña del usuario.
     * @return String con el resultado de la operacion.
     */
    @RequestMapping(value = "/users/{email}/{password}", method = RequestMethod.GET)
    public String loginUser(@PathVariable String email, @PathVariable String password)
    {
        LOG.info("[LOGIN] Peticion GET para loguear un usuario");
        LOG.info("[LOGIN]  -  Email: " + email);
        LOG.info("[LOGIN]  -  Contrasena: " + password);
        
        // Buscamos el usuario con el email y la contraseña.
        User user = usersRepository.findByEmailAndPassword(email, password);
        
        // Si lo encontramos, se devuelve el ID.
        if (user != null)
        {
            LOG.info("[LOGIN] Usuario logeado correctamente (ID: " + user.getId() + ")");
            
            return String.valueOf(user.getId());
        }
        
        // Si no, se devuelve 'INCORRECT_LOGIN'
        LOG.info("[LOGIN] Usuario no encontrado, email y/o contrasena incorrectos");        
        return Properties.INCORRECT_LOGIN;
    }
    
    /**
     * Metodo que devuelve los productos favoritos de un usuario.
     * @param id: id del usuario.
     * @return lista de productos favoritos.
     */
    @RequestMapping(value = "/favorites/{id}", method = RequestMethod.GET)
    public List<Product> getFavoriteProducts(@PathVariable long id)
    {
        LOG.info("[PRODUCTS] Peticion GET para obtener los productos favoritos del usuario (ID: " + id + ")");
        
        User user = usersRepository.findOne(id);
        
        if (user == null)
        {
            LOG.warn("[PRODUCTS] No se encuentra el usuario (ID: " + id + ")");
            
            return new ArrayList<>();
        }
        
        List<Product> productList = new ArrayList<>();
        for (long productId : user.getFavoriteProducts())
        {
            productList.add(productsRepository.findOne(productId));
        }
        
        LOG.info("[PRODUCTS] Usuario encontrado, tiene " + productList.size() + " favoritos");
        return productList;
    }
    
    /**
     * Metodo que añade las tiendas favoritas de un usuario.
     * @param id: id del usuario.
     * @param shops: lista de tiendas favoritas.
     * @return resultado de la operacion.
     */
    @RequestMapping(value = "/shops/{id}", method = RequestMethod.POST)
    public String addShopsToUser(@PathVariable long id, @RequestBody Set<String> shops)
    {
        LOG.info("[UPDATE] Peticion POST para anadir las tiendas del usuario (ID: " + id + ")");
        User user = usersRepository.findOne(id);
        
        if (user == null)
        {
            LOG.warn("[UPDATE] No se encuentra al usuario (ID: " + id + ")");
            return Properties.USER_NOT_FOUND;
        }
        
        LOG.info("[UPDATE] Usuario encontrado, se actualizan sus tiendas");
        user.setShops(shops);
        
        usersRepository.save(user);
        
        return Properties.ACCEPTED;
    }
    
    /**
     * Metodo que añade una tienda.
     * @param shop: tienda a añadir.
     * @return true si ha ido correctamente.
     */
    @RequestMapping(value = "/shops", method = RequestMethod.POST)
    public String addShop(@RequestBody Shop shop)
    {
        shopsRepository.save(shop);
        
        return Properties.ACCEPTED;
    }
    
    /**
     * Metodo que devuelve la lista de tiendas.
     * @param gender: sexo de la tienda.
     * @return lista de tiendas de hombre o de mujer.
     */
    @Cacheable(value = "products")
    @RequestMapping(value = "/shops/{gender}", method = RequestMethod.GET)
    public List<Shop> getShops(@PathVariable boolean gender)
    {
        LOG.info("[SHOPS] Peticion GET para obtener la lista de todas las tiendas");       
        
        return (gender) ? shopsRepository.findByMan() : shopsRepository.findByWoman();
    }
    
    /**
     * Metodo que añade el producto a la lista correspondiente de la UserActivity
     * @param userId: id del usuario.
     * @param productId: id del producto.
     * @param action: accion que ha realizado el usuario con el producto.
     * @return identificador del producto o un error.
     */
    @RequestMapping(value = "/users/{userId}/{productId}/{action}", method = RequestMethod.GET)
    public String addProductToUserActivity(@PathVariable long userId
                                , @PathVariable long productId
                                , @PathVariable short action)
    {
        LOG.info("[UPDATE] Peticion GET para anadir un producto al UserActivity del usuario (ID: " + userId + ")");
        
        User user = usersRepository.findOne(userId);
        Product product = productsRepository.findOne(productId);
        
        if (user == null)
        {
            LOG.warn("[UPDATE] No se encuentra el usuario (ID :" + userId + ")");            
            return Properties.USER_NOT_FOUND;
        }
        
        if (product == null)
        {
            LOG.warn("[UPDATE] No se encuentra el producto (ID: " + productId + ")");            
            return Properties.PRODUCT_NOT_FOUND;
        }
        
        switch(action)
        {
            case Properties.ACTION_ADDED_TO_CART:
                
                LOG.info("[UPDATE] Producto (" + productId + ") anadido al carrito del usuario (ID: " + userId + ")");
                user.addToAddedToCartProducts(productId);
                
                break;
                
            case Properties.ACTION_FAVORITE:
                
                if (!user.getFavoriteProducts().contains(productId))
                {
                    LOG.info("[UPDATE] Producto (" + productId + ") anadido a favoritos del usuario (ID: " + userId + ")");
                    user.addToFavoriteProducts(productId);
                    
                } else {
                    LOG.info("[UPDATE] Producto (" + productId + ") quitado de favoritos del usuario (ID: " + userId + ")");
                    user.getFavoriteProducts().remove(productId);
                }
                
                break;
                
            case Properties.ACTION_VIEWED:
                
                LOG.info("[UPDATE] Producto (" + productId + ") visto por el usuario (ID: " + userId + ")");
                user.addToViewedProducts(productId);
                
                break;
                
            case Properties.ACTION_VISITED:
                
                LOG.info("[UPDATE] Producto (" + productId + ") visitado en la web por el usuario (ID: " + userId + ")");
                user.addToVisitedProducts(productId);
                
                break;
                
            default:
                
                LOG.warn("[UPDATE] Accion (" + action + ") incorrecta");
                return Properties.INCORRECT_ACTION;
        }
        
        usersRepository.save(user);
        
        return Properties.ACCEPTED;
    }
    
    /**
     * Metodo que elimina los productos de la tienda e inserta los nuevos recibidos.
     * @param products: Lista de los productos a insertar.
     * @param shop: Tienda a la que pertenecen los productos.
     * @return Codigo HTTP con el resultado de la ejecucion.
     */
    @CacheEvict(value = "products", allEntries = true)
    @RequestMapping(value = "/products/{shop}", method = RequestMethod.POST)
    public ResponseEntity<Boolean> addProducts(@RequestBody List<Product> products
                                        , @PathVariable String shop)
    {
        LOG.info("[SCRAPER] Peticion POST para anadir productos de " + shop + " recibida");
        
        Runnable task = () -> {                 
            // Obtenemos los productos que ya tenemos en base de datos.
            List<Product> productsInDB = productsRepository.findByShop(shop);
            
            LOG.info("[SCRAPER] Llamando a ImageManager para descargar las imagenes que no existan");
            List<Product> productsScraped = ImageManager.downloadImages(products, shop);
            
            if (!productsInDB.isEmpty())
            {
                Shop s = shopsRepository.findByName(shop);                
                s.setProducts(productsScraped.size());                
                shopsRepository.save(s);
                
                // Si hay productos de esta tienda en BD, miramos que productos necesitan actualizarse o añadirse.
                for (Product productScraped : productsScraped)
                {
                    boolean found = false;
                    int i = 0;
                    
                    productScraped.setAspectRatio(ImageManager.getAspectRatio(shop));
                    
                    // Buscamos el producto scrapeado en la lista sacada de BD.
                    while ((!found) && (i < productsInDB.size()))
                    {
                        Product productInDB = productsInDB.get(i++);

                        // Miramos si el producto es igual, distinto, o el mismo pero con alguna modificacion.
                        int comparison = productInDB.compare(productInDB, productScraped);

                        // Si es el mismo producto.
                        if (comparison >= 0)
                        {
                            // Actualizamos el producto.
                            LOG.info("[SCRAPER] Producto encontrado, se actualiza" + ((comparison == 0) ? "" : " y se anade como novedad"));
                            productInDB.update(productScraped, (comparison == 0));

                            // Y lo guardamos.
                            productsRepository.save(productInDB);

                            found = true;
                        }
                    }
                    
                    if (!found)
                    {
                        LOG.info("[SCRAPER] Producto NO encontrado, lo insertamos como novedad");
                        productScraped.setInsertDate(Calendar.getInstance());

                        productsRepository.save(productScraped);  
                    }                        
                }
                
                LOG.info("[SCRAPER] Productos actualizados, se borran los productos antiguos");
                productsInDB = productsRepository.findByShop(shop);
                for (Product productInDB : productsInDB)
                {
                    boolean found = false;
                    int i = 0;
                    while ((!found) && (i < productsScraped.size()))
                    {
                        Product productScraped = productsScraped.get(i++);

                        found = (productScraped.compare(productScraped, productInDB) >= 0);
                    }

                    if (!found)
                    {
                        LOG.info("[SCRAPER] Producto NO encontrado, se marca como OBSOLETO");
                        productInDB.setObsolete(true);
                        productsRepository.save(productInDB);
                    }
                } 
                
            } else {         
                boolean woman = false;
                boolean man = false;
                
                // Buscamos si tiene seccion de hombre y/o mujer.
                for (Product productScraped : productsScraped)
                {
                    if (productScraped.isMan())
                    {
                        man = true;
                        
                    } else {
                        woman = true;
                    }
                    
                    if (man && woman)
                    {
                        break;
                    }
                }
                
                // Se añade la tienda.
                shopsRepository.save(new Shop(shop, man, woman, productsScraped.size()));
                
                LOG.info("[SCRAPER] No hay ningun producto de la tienda " + shop);
                LOG.info("[SCRAPER] Los productos se insertan directamente");
                for (Product productScraped : productsScraped)
                {
                    productScraped.setAspectRatio(ImageManager.getAspectRatio(shop));
                    productScraped.setInsertDate(Calendar.getInstance());
                    
                    productsRepository.save(productScraped);
                }                
            }
            
            LOG.info("[SCRAPER] " + productsScraped.size() + " productos de " + shop + " insertados correctamente");        
            LOG.info("[SCRAPER] Saliendo del metodo addProducts");
        };
        
        EXECUTOR.execute(task);       
                
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /**
     * Metodo que devuelve una lista de productos de una tienda.
     * @param shop: Tienda de la que se quieren los productos.
     * @return Lista de productos.
     */
    @RequestMapping(value = "/products/{shop}", method = RequestMethod.GET)
    public List<Product> getProducts(@PathVariable String shop)
    {
        LOG.info("[PRODUCTS] Peticion GET para obtener todos los productos de " + shop);
        return productsRepository.findByShop(shop);
    }
    
    /**
     * Metodo que devuelve la lista de productos recomendados de un usuario.
     * @param id: id del usuario.
     * @return Lista de productos recomendados.
     */
    @RequestMapping(value = "/recommended/{id}", method = RequestMethod.GET)
    public List<Product> getRecommendedProducts(@PathVariable long id)
    {
        LOG.info("[PRODUCTS] Peticion GET para obtener todos los productos recomendados del usuario (ID: " + id + ")");
        
        List<Product> aux = productsRepository.findByManAndShop(false, "Blanco");
        
        List<Product> recommendedProducts = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            Random rand = new Random();

            int randomNum = rand.nextInt((50 - 1) + 1) + 1;

            recommendedProducts.add(aux.get(randomNum));
        }
        
        return recommendedProducts;
    }
    
    /**
     * Metodo que devuelve una lista de novedades de una tienda
     * @param shop: Tienda de la que se quieren las novedades.
     * @param man: true si se quiere solo los productos de hombre.
     * @param offset: numero de dias del que se quiere los productos.
     * @return Lista de productos.
     */
    @Cacheable(value = "products", key = "#shop.toString() + #man.toString() + #offset.toString()")
    @RequestMapping(value = "/products/{shop}/{man}/{offset}", method = RequestMethod.GET)
    public List<Product> getProductsByShopAndByDay(@PathVariable String shop
                            , @PathVariable String man
                            , @PathVariable String offset)
    {
        LOG.info("[PRODUCTS] Peticion GET para obtener los productos de " + shop + " de hace " + offset + " dias");
        return productsRepository.findByShopAndDate(shop, Boolean.valueOf(man), Integer.valueOf(offset) + 15) ;
    }
    
    /**
     * Metodo que devuelve una lista de productos de una seccion de una tienda.
     * @param shop: Tienda a la que pertenece la seccion.
     * @param section: Seccion de la que se quieren los productos.
     * @return Lista de productos.
     */
    @RequestMapping(value = "/products/{shop}/{section}", method = RequestMethod.GET)
    public List<Product> getProductsBySection(@PathVariable String shop
                                , @PathVariable String section)
    {
        LOG.info("[PRODUCTS] Peticion GET para obtener los productos de la seccion de " + section + " de la tienda " + shop);
        return productsRepository.findBySectionAndShop(section, shop) ;
    }
    
    /**
     * Metodo que devuelve una lista de productos que cumplen una serie de condiciones.
     * @param filter: Filtro por el que tienen que pasar los productos.
     * @param id: id del usuario.
     * @param shop: Tienda de la que se quiere filtrar los productos.
     * @return Lista de productos.
     */
    @RequestMapping(value = "/filter/{id}/{shop}", method = RequestMethod.POST)
    public List<Product> getProductsByFilter(@RequestBody Filter filter
                                    , @PathVariable long id
                                    , @PathVariable String shop)
    {
        LOG.info("[FILTER] Peticion GET para obtener los productos de " + shop + " por el usuario (ID: " + id + ")");
        
        User user = usersRepository.findOne(id);
        
        if (user == null)
        {
            LOG.warn("[FILTER] Usuario no encontrado (ID: " + id + ")");
            
            return new ArrayList<>();
            
        } else {
            LOG.info("[FILTER] Usuario encontrado, se procede a la busqueda de los productos que cumplan los siguientes filtros:");
            
            Set<String> filters = user.getFilters();
            
            filters.add(filter.toString());
            user.setFilters(filters);
            
            usersRepository.save(user);
        }
        
        List<Product> productList;
        
        if (filter.isMan())
        {
            LOG.info("[FILTER]  -  Solo hombre");
        } else {
            LOG.info("[FILTER]  -  Solo mujer"); 
        }
            
        if (filter.getPriceFrom() > 0)
        {
            LOG.info("[FILTER]  -  Precio minimo = " + filter.getPriceFrom());
        }
            
        if (filter.getPriceTo() > 0)
        {
            LOG.info("[FILTER]  -  Precio maximo = " + filter.getPriceTo()); 
        }
            
        // Ponemos un valor minimo y maximo si no se reciben en el JSON.
        double from = (filter.getPriceFrom() > 0) ? filter.getPriceFrom() : -1;
        double to = (filter.getPriceTo() > 0) ? filter.getPriceTo() : 999; 

        if (filter.isNewness())
        {
            LOG.info("[FILTER]  -  Solo novedades");                                 

            productList = productsRepository.findByShopAndManAndNewnessAndPrice(shop
                                        , filter.isMan()
                                        , 0
                                        , from
                                        , to);

        } else {
            LOG.info("[FILTER]  -  Todos los productos");                                 

            productList = productsRepository.findByShopAndManAndPrice(shop
                                        , filter.isMan()
                                        , from
                                        , to);
        }       
           
        List<Product> newList = new ArrayList<>();
        
        // Buscamos primero si tiene el filtro de color y de secciones
        if (!filter.getSections().isEmpty() && !filter.getColors().isEmpty())
        {
            LOG.info("[FILTER]  -  De las siguientes secciones:");            
            for (String section : filter.getSections())
            {
                LOG.info("[FILTER]    " + section);   
            }                
                
            LOG.info("[FILTER]  -  De los siguientes colores:");            
            for (String color : filter.getColors())
            {
                LOG.info("[FILTER]    " + color);  
            }
                
            for (Product product : productList)
            {
                if (_searchForSection(product, filter.getSections()))
                {
                    Product aux = _searchForColor(product, filter.getColors());
                    if (aux != null)
                    {
                        newList.add(aux);    
                    }
                }
            }
            
            if (newList.size() > Properties.MAX_FILTERED_PRODUCTS)
            {
                newList = newList.subList(0, Properties.MAX_FILTERED_PRODUCTS - 1);
            }
            
            return newList;
        }
        
        // Buscamos la seccion si no tiene el filtro de color
        if (!filter.getSections().isEmpty() && filter.getColors().isEmpty())
        {         
            LOG.info("[FILTER]  -  De las siguientes secciones:");            
            for (String section : filter.getSections())
            {
                LOG.info("[FILTER]    " + section);   
            }
                
            for (Product product : productList)
            {
                if (_searchForSection(product, filter.getSections()))
                {
                    newList.add(product);  
                }
            }
            
            if (newList.size() > Properties.MAX_FILTERED_PRODUCTS)
            {
                newList = newList.subList(0, Properties.MAX_FILTERED_PRODUCTS - 1);
            }
            
            return newList;
        }  

        // Buscamos el color si no tiene el filtro de secciones
        if (filter.getSections().isEmpty() && !filter.getColors().isEmpty())
        {   
            LOG.info("[FILTER]  -  De los siguientes colores:");            
            for (String color : filter.getColors())
            {
                LOG.info("[FILTER]    " + color);         
            }
                
            for (Product product : productList)
            {
                Product aux =_searchForColor(product, filter.getColors());
                if (aux != null)
                {
                    newList.add(product);
                }
            }
            
            if (newList.size() > Properties.MAX_FILTERED_PRODUCTS)
            {
                newList = newList.subList(0, Properties.MAX_FILTERED_PRODUCTS - 1);
            }
            
            return newList;
        }   
        
        if (productList.size() > Properties.MAX_FILTERED_PRODUCTS)
        {
            productList = productList.subList(0, Properties.MAX_FILTERED_PRODUCTS - 1);
        }
        
        return productList;
    }
    
    /**
     * Metodo que realiza una busqueda de productos.
     * @param id: id del usuario.
     * @param search: productos a buscar.
     * @return lista de productos encontrados.
     */
    @RequestMapping(value = "/search/{id}/{search}", method = RequestMethod.GET)
    public List<Product> getProductsBySearch(@PathVariable long id
                                , @PathVariable String search)
    {
        LOG.info("[SEARCH] Peticion GET para buscar (" + search + ") por el usuario (ID: " + id + ")" );
        
        List<Product> newList = new ArrayList<>();
        String[] aux = search.split(" "); 
        
        User user = usersRepository.findOne(id);
        
        if (user == null)
        {
            LOG.warn("[SEARCH] Usuario no encontrado (ID: " + id + ")");
            
            return new ArrayList<>();
            
        } else {
            LOG.info("[SEARCH] Usuario encontrado, se procede a la busqueda de: " + search);
            
            Set<String> searches = user.getSearches();
            
            searches.add(search);
            user.setSearches(searches);
            
            usersRepository.save(user);
        }
        
        // Sacamos los productos de sus tiendas.
        List<Product> productList = new ArrayList<>();
        for (String shop : user.getShops())
        {
            productList.addAll(productsRepository.findByManAndShop(user.getMan(), shop));
        }
        
        // Eliminamos palabras irrelevantes ('a', 'de', 'con', etc.)
        List<String> keywords = new ArrayList<>();
        for (String keyword : aux)
        {
            if ((keyword.length() > 2) && (!keyword.equalsIgnoreCase("con")))
            {
                keywords.add(keyword);
            }
        }
        
        // Recorremos los productos 
        for (Product product : productList)
        {
            boolean candidate = true;            
            Product paux = null;
            
            // Recorremos las palabras buscadas
            for (String keyword : keywords)
            {
                // Comprobamos si es una seccion
                List<String> section = new ArrayList<>();
                String saux = sectionManager.getSection(keyword);
                if (saux != null)
                {
                    section.add(saux);
                }
                    
                if (!section.isEmpty())
                {
                    candidate = _searchForSection(product, section);
                    
                    if (!candidate)
                    {
                        break;
                    }
                }
                
                // Comprobamos si es un color
                List<String> color = new ArrayList<>();
                String caux = colorManager.getColor(keyword);
                if (caux != null)
                {
                    color.add(caux);
                }
                
                if (!color.isEmpty())
                {
                    paux = _searchForColor(product, color);
                    
                    candidate = (paux != null);
                    if (!candidate)
                    {
                        break;
                    }
                }
                
                // Si no es ni seccion ni color
                if (color.isEmpty() && section.isEmpty())
                {
                    candidate = _searchForKeyword(product, keyword);
                    
                    if (!candidate)
                    {
                        break;
                    }
                }                
            }
            
            if (candidate)
            {
                newList.add((paux == null) ? product : paux);
            }
        }
        
        return newList;
    }
    
    /**
     * Metodo que devuelve una lista de sugerencias.
     * @param word palabras buscadas.
     * @return lista de sugerencias.
     */
    @RequestMapping(value = "/suggest/{word}", method = RequestMethod.GET)
    public List<String> getSuggestions(@PathVariable String word)
    {
        List<String> suggestions = new ArrayList<>();
        String[] words = word.split(" ");
        
        // Si solo recibimos un palabra, buscamos una seccion.
        if (words.length == 1)
        {            
            suggestions = sectionManager.getSectionsStartingWith(word);
        }
        
        // Si recibimos dos palabras, buscamos la primera palabra como seccion
        // la segunda se busca como color
        if (words.length == 2)
        {
            List<String> firstWordSuggestions = sectionManager.getSectionsStartingWith(words[0]);
            
            if (firstWordSuggestions.isEmpty())
                return new ArrayList<>();
            
            List<String> colors = colorManager.getColorStartingWith(words[1]);
            
            if (colors == null)
                return new ArrayList<>();
            
            for (String firstWordSuggestion : firstWordSuggestions)
            {                
                for (String color : colors)
                {
                    suggestions.add(firstWordSuggestion + " " 
                        + ((sectionManager.getSectionGender(firstWordSuggestion)) ? color : colorManager.getFemaleColor(color)));
                }    
            }
        }
        
        return suggestions;
    }
    
    /**
     * Metodo que busca en el producto los colores recibidos.
     * @param product: producto en el que buscar los colores.
     * @param colors: colores que buscar en el producto.
     * @return producto con el color buscado en primera posicion, null si no esta el color.
     */
    private Product _searchForColor(Product product, List<String> colors)
    {                
        colors = colorManager.getEquivalentColors(colors);
        
        // Buscamos primero en el color
        for (String color : colors)
        {
            int pos = 0;
            for (ColorVariant cv : product.getColors())
            {
                String[] decomposedColor = cv.getName().split(" ");
                
                for (String single : decomposedColor)
                {
                    single = single.replaceAll("[0-9]" , "");
                    
                    if (org.apache.commons.lang3.StringUtils
                                .getJaroWinklerDistance(color
                                        , single) >= Properties.MAX_SIMILARITY_THRESHOLD)
                    {
                        LOG.info("[SEARCH] Color '" + color + "' encontrado: " + single);
                        
                        return _reorderColorVariants(product, pos);
                    }
                }
                
                pos++;
            }            
        } 
        
        // Si no encontramos nada en el color, lo buscamos en el nombre
        for (String color : colors)
        {
            String[] decomposedName = product.getName().split(" ");
            
            for (String single : decomposedName)
            {
                single = single.replace("," , "").replace("." , "").replace("\n", "").trim();
                
                if (org.apache.commons.lang3.StringUtils
                                .getJaroWinklerDistance(color
                                        , single) >= Properties.MAX_SIMILARITY_THRESHOLD)
                {
                    LOG.info("[SEARCH] Color '" + color + "' encontrado: " + single);
                    
                    return product;
                }
            }  
        }
        
        return null;
    }
    
    /**
     * Metodo que busca en el producto las secciones recibidas.
     * @param product: producto en el que buscar las secciones.
     * @param sections: secciones que buscar en el producto.
     * @return true si alguna seccion esta en el producto.
     */
    private boolean _searchForSection(Product product, List<String> sections)
    {           
        sections = sectionManager.getEquivalentSections(sections);
        
        // Buscamos primero en el campo seccion
        for (String section : sections)
        {
            if (org.apache.commons.lang3.StringUtils
                                .getJaroWinklerDistance(section
                                        , product.getSection()) >= Properties.MAX_SIMILARITY_THRESHOLD)
            {
                LOG.info("[SEARCH] Seccion '" + section + "' encontrada: " + product.getSection());
                
                return true;
            }
        }
        
        // Si no encontramos nada en el campo seccion, buscamos en el nombre
        for (String section : sections)
        {
            String[] decomposedName = product.getName().split(" ");
            
            for (String single : decomposedName)
            {
                single = single.replace("," , "").replace("." , "").replace("\n", "").trim();
                
                if (org.apache.commons.lang3.StringUtils
                                .getJaroWinklerDistance(section
                                        , single) >= Properties.MAX_SIMILARITY_THRESHOLD)
                {
                    LOG.info("[SEARCH] Seccion '" + section + "' encontrada: " + single);
                    
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Metodo que coloca el color especificado en la primera posicion.
     * @param product: producto.
     * @param pos: posicion del color que hay que colocar en la primera posicion.
     * @return producto reordenado.
     */
    private Product _reorderColorVariants(Product product, int pos)
    {        
        if (product.getColors().size() > 1)
        {
            List<ColorVariant> aux = product.getColors();
            
            aux.add(0, aux.get(pos));
            aux.remove(pos + 1);
            
            product.setColors(aux);
        }
        
        return product;
    }
    
    /**
     * Metodo que busca una palabra clave en el producto.
     * @param product: producto donde buscar la palabra.
     * @param keyword: palabra a buscar.
     * @return true si el producto contiene la palabra.
     */
    private boolean _searchForKeyword(Product product, String keyword)
    {        
        // Buscamos la palabra en el nombre.        
        String[] decomposedName = product.getName().split(" ");
        for (String single : decomposedName)
        {
            single = single.replace("," , "").replace("." , "").replace("\n", "").trim();
            
            if (org.apache.commons.lang3.StringUtils
                            .getJaroWinklerDistance(keyword
                                    , single) >= Properties.MAX_SIMILARITY_THRESHOLD)
            {
                LOG.info("[SEARCH] Keyword '" + keyword + "' encontrado: " + single);

                return true;
            }
        }  
        
        // Si no la encontramos en el nombre, la buscamos en la descripcion.
        decomposedName = product.getDescription().split(" ");            
        for (String single : decomposedName)
        {
            single = single.replace("," , "").replace("." , "").replace("\n", "").trim();
            
            if (org.apache.commons.lang3.StringUtils
                            .getJaroWinklerDistance(keyword
                                    , single) >= Properties.MAX_SIMILARITY_THRESHOLD)
            {
                LOG.info("[SEARCH] Keyword '" + keyword + "' encontrado: " + single);

                return true;
            }
        }
        
        return false;
    }
}
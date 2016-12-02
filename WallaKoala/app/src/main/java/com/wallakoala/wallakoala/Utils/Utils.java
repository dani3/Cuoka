package com.wallakoala.wallakoala.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Clase con metodos varios.
 * Created by Daniel Mancebo Aldea on 02/02/2016.
 */

public class Utils
{
    public static Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    /**
     * Metodo que dado una diferencia de dias, devuelve un mensaje.
     * @param offset: diferencia de dias.
     * @return mensaje descriptivo de la diferencia de dias.
     */
    @Nullable
    public static String getMessageFromDaysOffset(short offset)
    {
        if (offset == 0) {
            return "Hoy";

        } else if (offset == 1) {
            return "Ayer";

        } else if (offset > 1 && offset < 7) {
            return "Hace " + offset + " días";

        } else if (offset >= 7 && offset < 12) {
            return "Hace una semana";

        } else if (offset >= 12) {
            return "Hace dos semanas";
        }

        return null;
    }

    /**
     * Metodo que parsea un JSON en usuario.
     * @param id: id del usuario.
     * @param jsonObject: objeto JSON a parsear.
     * @return objeto usuario.
     * @throws JSONException
     */
    public static User getUserFromJSON(long id, JSONObject jsonObject) throws JSONException
    {
        User user = new User();

        user.setId(id);
        user.setName(jsonObject.getString("name"));
        user.setAge(jsonObject.getInt("age"));
        user.setEmail(jsonObject.getString("email"));
        user.setPassword(jsonObject.getString("password"));
        user.setMan(jsonObject.getBoolean("man"));
        user.setPostalCode(jsonObject.getInt("postalCode"));

        // Sacamos los productos favoritos
        JSONArray jsonArray = jsonObject.getJSONArray("favoriteProducts");
        Set<Long> favorites = new HashSet<>();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            favorites.add(Long.valueOf((String.valueOf(jsonArray.get(i)))));
        }

        user.setFavoriteProducts(favorites);

        // Sacamos la lista de tiendas
        jsonArray = jsonObject.getJSONArray("shops");
        Set<String> shops = new HashSet<>();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            shops.add((String.valueOf(jsonArray.get(i))));
        }

        user.setShops(shops);

        Log.d(Properties.TAG, "Datos del usuario: ");
        Log.d(Properties.TAG, " - ID: " + id);
        Log.d(Properties.TAG, " - Nombre: " + user.getName());
        Log.d(Properties.TAG, " - Email: " + user.getAge());
        Log.d(Properties.TAG, " - Contraseña: " + user.getPassword());
        Log.d(Properties.TAG, " - Hombre: " + user.getMan());
        Log.d(Properties.TAG, " - Edad: " + user.getAge());
        Log.d(Properties.TAG, " - Codigo Postal: " + user.getPostalCode());
        Log.d(Properties.TAG, " - Numero de favoritos: " + user.getFavoriteProducts().size());
        Log.d(Properties.TAG, " - Tiendas: " + jsonArray);

        return user;
    }

    /**
     * Metodo que devuelve la url de un icono.
     * @param colorVariant: color variant del icono.
     * @param shop: tienda del producto.
     * @param section: seccion del producto.
     * @return url del icono del color.
     */
    public static String getColorUrl(ColorVariant colorVariant, String shop, String section)
    {
        String url;

        if (colorVariant.getColorPath().equals("0"))
        {
            final String imageFile = shop + "_" + section + "_"
                    + colorVariant.getReference() + "_"
                    + colorVariant.getColorName().replaceAll(" ", "_") + "_ICON.jpg";

            url = Utils.fixUrl(Properties.SERVER_URL + Properties.ICONS_PATH + shop + "/" + imageFile);

        } else {
            final String imageFile = colorVariant.getColorPath();

            url = Utils.fixUrl(Properties.SERVER_URL + Properties.PREDEFINED_ICONS_PATH + imageFile + "_ICON.jpg");
        }

        return url;
    }

    /**
     * Metodo que precarga las imagenes de un producto.
     * @param context: contexto.
     * @param product: producto a precargar.
     * @param currentColor: posicion del color a precargar.
     */
    public static void fetchImages(Context context, Product product, int currentColor)
    {
        ColorVariant colorVariant = product.getColors().get(currentColor);
        for (int i = 0; i < colorVariant.getNumberOfImages(); i++)
        {
            String imageFile = product.getShop() + "_" + product.getSection() + "_"
                    + colorVariant.getReference() + "_"
                    + colorVariant.getColorName() + "_" + i + "_Large.jpg";

            String url = Utils.fixUrl(
                    Properties.SERVER_URL + Properties.IMAGES_PATH + product.getShop() + "/" + imageFile);

            // Pre-Cargamos la imagen utilizando Picasso.
            Picasso.with(context)
                    .load(url)
                    .fetch();
        }

        for (int i = 0; i < product.getColors().size(); i++)
        {
            // Path != 0 -> Color predefinido
            String url;
            if (product.getColors().get(i).getColorPath().equals("0"))
            {
                final String imageFile = product.getShop() + "_" + product.getSection() + "_"
                        + product.getColors().get(i).getReference() + "_"
                        + product.getColors().get(i).getColorName().replaceAll(" ", "_") + "_ICON.jpg";

                url = Utils.fixUrl(Properties.SERVER_URL + Properties.ICONS_PATH + product.getShop() + "/" + imageFile);

            } else {
                final String imageFile = product.getColors().get(i).getColorPath();

                url = Utils.fixUrl(Properties.SERVER_URL + Properties.PREDEFINED_ICONS_PATH + imageFile + "_ICON.jpg");
            }

            // Pre-Cargamos el icono utilizando Picasso.
            Picasso.with(context)
                    .load(url)
                    .fetch();
        }
    }

    /**
     * Metodo que codifica una URL.
     * @param url: URL a codificar.
     * @return URL codificada.
     */
    public static String fixUrl(final String url)
    {
        return url.replaceAll(" ", "%20");
    }

    /**
     * Metodo que da formato al precio.
     * @param price: double con el precio.
     * @return SpannableString con el precio formateado.
     */
    @SuppressLint("DefaultLocale")
    public static SpannableString priceToString(final double price)
    {
        final String sPrice = (String.format("%.2f", price) + "€").replaceAll(",00", "");
        final SpannableString sS = new SpannableString(sPrice);

        if (sPrice.contains(","))
            sS.setSpan(new RelativeSizeSpan(0.65f)
                    , sPrice.indexOf(",")
                    , sPrice.indexOf("€") + 1
                    , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        else
            sS.setSpan(new RelativeSizeSpan(0.65f)
                    , sPrice.indexOf("€")
                    , sPrice.indexOf("€") + 1
                    , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sS;
    }

    /**
     * Metodo que comprueba si un string contiene solo letras.
     * @param query: string a comprobar.
     * @return true si el string es correcto.
     */
    public static boolean isAlphaNumeric(final String query)
    {
        final char[] chars = query.toCharArray();

        for (char c : chars)
        {
            if (!Character.isLetter(c) && !Character.isSpaceChar(c) && !Character.isDigit(c))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Metodo que comprueba si un string contiene solo letras.
     * @param query: string a comprobar.
     * @return true si el string es correcto.
     */
    public static boolean isQueryOk(final String query)
    {
        final char[] chars = query.toCharArray();

        for (char c : chars)
        {
            if (!Character.isLetter(c) && !Character.isSpaceChar(c))
            {
                return false;
            }
        }

        return true;
    }

    @Nullable
    public static String saveImage(final Context context, final Bitmap bitmap, final int pos, final String TAG)
    {
        final String fileName = "thumbnail_" + pos + ".png";

        try
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            FileOutputStream fileOutStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutStream.write(byteArray);

            fileOutStream.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
            Log.e(TAG, "Error guardando la imagen");

            return null;
        }

        return fileName;
    }

    public static double mapValueFromRangeToRange(double value
                                    , double fromLow
                                    , double fromHigh
                                    , double toLow
                                    , double toHigh)
    {
        return toLow + ((value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow));
    }

    public static double clamp(double value, double low, double high)
    {
        return Math.min(Math.max(value, low), high);
    }

    /**
     * Metodo que comprueba si el email es correcto.
     * @param email: email a comprobar.
     * @return true si el email es correcto.
     */
    public static boolean isValidEmail(final String email)
    {
        return (!TextUtils.isEmpty(email) &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    /**
     * Metodo que comprueba si la edad es correcta.
     * @param age: edad a comprobar.
     * @return true si la edad es correcta.
     */
    public static boolean isValidAge(final String age)
    {
        return (!age.isEmpty()) && (Integer.valueOf(age) >= Properties.MIN_AGE && Integer.valueOf(age) <= Properties.MAX_AGE);
    }

    /**
     * Metodo que comprueba si el CP es correcto.
     * @param postalCode: CP a comprobar.
     * @return true si el CP es correcto.
     */
    public static boolean isValidPostalCode(final String postalCode)
    {
        return (postalCode.length() == Properties.POSTAL_CODE_LENGHT);
    }

    /**
     * Metodo que comprueba si la contraseña es correcta.
     * @param password: contraseña a comprobar.
     * @return true si la contraseña es correcta.
     */
    public static boolean isValidPassword(final String password)
    {
        return (!password.trim().isEmpty() &&
                !(password.trim().length() < Properties.MIN_PASSWORD_LENGTH) &&
                !(password.toUpperCase().contains("SELECT")) && !(password.toUpperCase().contains("DROP")) &&
                !(password.toUpperCase().contains("DELETE")) && !(password.toUpperCase().contains("UPDATE")) &&
                !(password.contains("*")) && !(password.contains("/")) && !(password.contains("\\")) &&
                !(password.contains("=")) && !(password.contains("|")) && !(password.contains("&")) &&
                !(password.contains("'")) && !(password.contains("!")) && !(password.contains(";")));
    }

    /**
     * Metodo que comprueba si el nombre es correcto.
     * @param name: nombre a comprobar.
     * @return true si el nombre es correcto.
     */
    public static boolean isValidName(final String name)
    {
        return isQueryOk(name);
    }
}

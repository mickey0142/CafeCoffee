package Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coffee.cafe.app.mobile.cafecoffee.GlideApp;
import com.coffee.cafe.app.mobile.cafecoffee.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import Model.Shop;

public class ShopListAdapter extends ArrayAdapter {

    Context context;
    ArrayList<Shop> shop;
    FirebaseStorage fbStorage = FirebaseStorage.getInstance();

    public ShopListAdapter(Context context, int  resource, ArrayList<Shop> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.shop = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View shopItem = LayoutInflater.from(context).inflate(R.layout.fragment_shop_list_item, parent, false);
        ImageView shopPicture = shopItem.findViewById(R.id.shop_list_item_shop_picture);
        TextView shopName = shopItem.findViewById(R.id.shop_list_item_shop_name);
        shopName.setText(shop.get(position).getShopName());
        StorageReference imageRef = fbStorage.getReferenceFromUrl("gs://cafe-coffee-576ed.appspot.com")
                .child(shop.get(position).getPictureName());
        GlideApp.with(getContext()).load(imageRef).into(shopPicture);
        return shopItem;
    }
}

package com.milan.dukan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.milan.dukan.adapters.CategoryRecyclerAdapter;
import com.milan.dukan.adapters.ProductRecyclerAdapter;
import com.milan.dukan.models.Category;
import com.milan.dukan.models.Product;
import com.milan.dukan.utils.Constants;
import com.milan.dukan.utils.Utils;
import com.milan.dukan.views.BaseActivity;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements
        ProductRecyclerAdapter.OnProductListChangedListener,
        CategoryRecyclerAdapter.OnCategoryListChangedListener,
        CategoryRecyclerAdapter.OnCategoryClickListener,
        ProductRecyclerAdapter.OnProductClickListener {

    private final ArrayList<Category> mCategories = Utils.categories;

    // UI Components
    RecyclerView rvProducts, rvCategories;
    TextView tvProductsLabel, tvCategoriesLabel;
    SharedPreferences sp;
    // vars
    private ArrayList<Product> mProducts = new ArrayList<>();
    private ProductRecyclerAdapter mProductRecyclerAdapter;
    private CategoryRecyclerAdapter mCategoryRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences(Constants.APP_PREFERENCE_NAME, Context.MODE_PRIVATE);

        // set custom toolbar as support action bar to activity
        setSupportActionBar(findViewById(R.id.main_toolbar));

        bindViews();

        mProducts = Utils.insertFakeProducts();
        initRecyclerView();
    }

    private void bindViews() {
        rvProducts = findViewById(R.id.main_products);
        rvCategories = findViewById(R.id.main_categories);
        tvProductsLabel = findViewById(R.id.products_label);
        tvCategoriesLabel = findViewById(R.id.categories_label);
    }

    private void initRecyclerView() {
        mProductRecyclerAdapter = new ProductRecyclerAdapter(mProducts, this, this);
        rvProducts.setAdapter(mProductRecyclerAdapter);
        mCategoryRecyclerAdapter = new CategoryRecyclerAdapter(this, mCategories, this, this);
        rvCategories.setAdapter(mCategoryRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        // remove underline from search view
        searchView.findViewById(androidx.appcompat.R.id.search_plate).setBackgroundColor(Color.TRANSPARENT);
        // make search view to take entire width
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mProductRecyclerAdapter.getFilter().filter(query);
                mCategoryRecyclerAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // filter recycler view when text is changed
                mProductRecyclerAdapter.getFilter().filter(newText);
                mCategoryRecyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.logout) {
            SharedPreferences.Editor spEditor = sp.edit();
            spEditor.clear();
            spEditor.apply();
            spEditor.commit();
            displayToast("Sign Out Successfully.");
            navigate(LoginActivity.class);
            finishAffinity();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCategoryListChanged() {
        if (mCategoryRecyclerAdapter.getItemCount() == 0) {
            rvCategories.setVisibility(View.GONE);
            tvCategoriesLabel.setVisibility(View.GONE);
        } else {
            rvCategories.setVisibility(View.VISIBLE);
            tvCategoriesLabel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProductListChanged() {
        if (mProductRecyclerAdapter.getItemCount() == 0) {
            rvProducts.setVisibility(View.GONE);
            tvProductsLabel.setVisibility(View.GONE);
        } else {
            rvProducts.setVisibility(View.VISIBLE);
            tvProductsLabel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCategoryClick(int position) {
        Category category = mCategories.get(position);
        Intent intent = new Intent(this, ProductListActivity.class);
        intent.putExtra("selected_category", category);
        startActivity(intent);
    }

    @Override
    public void onProductClick(int position) {
        Product product = mProducts.get(position);
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("selected_product", product);
        Category catOfProduct = Utils.getCategory(product.getCategoryId());
        if (catOfProduct != null) {
            intent.putExtra("selected_category", catOfProduct);
        }
        startActivity(intent);
    }
}
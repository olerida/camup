package com.segre.camup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class Gallery2Activity extends AppCompatActivity {

    ArrayList<Integer> okfoto = new ArrayList<Integer>();
    Integer[] fotook;
    public String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery2);

        final GridView gridview = (GridView) findViewById(R.id.gallery2_view);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera";

        gridview.setAdapter(new ImageAdapter(this, path));
        gridview.setChoiceMode(gridview.CHOICE_MODE_MULTIPLE);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SparseBooleanArray sb = gridview.getCheckedItemPositions();
                gridview.setItemChecked(position, sb.get(position));

                Log.d("CHECKED", "" + sb.get(position));
                if(sb.get(position) == true)
                {
                    okfoto.add(position);
                    RelativeLayout rl = (RelativeLayout) view;
                    ImageView ck = (ImageView) rl.getChildAt(1);
                    ck.setVisibility(View.VISIBLE);
                }
                else {
                    okfoto.remove(new Integer(position));
                    RelativeLayout rl = (RelativeLayout) view;
                    ImageView ck = (ImageView) rl.getChildAt(1);
                    ck.setVisibility(View.INVISIBLE);
                }
                for(int x=0;x<okfoto.size();x++) {
                    System.out.println("ArrayList"+ okfoto.get(x));
                }
                fotook = okfoto.toArray(new Integer[okfoto.size()]);
                /*for(int x=0;x<fotook.length;x++) {
                    System.out.println("Array "+ fotook[x].toString());
                }*/

                Toast.makeText(Gallery2Activity.this, "" + position, Toast.LENGTH_SHORT).show();


            }

        });


    }

    public void eliminarFotos(View view) {
        //Borrar Imatges seleccionades


        //Borrar ArrayList
        okfoto.clear();
        System.out.println("Elements de l'Array seleccionada:"+ okfoto.size());

    }


    public class ImageAdapter extends BaseAdapter {

        private Context mContext;
        File[] files;

        public ImageAdapter(Context c, String path) {
            mContext = c;
            File imagesDir = new File(path);
            files = imagesDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return (name.endsWith(".jpg") ||
                            name.endsWith(".jpeg") ||
                            name.endsWith(".nef") ||
                            name.endsWith(".nrw") ||
                            name.endsWith(".crw") ||
                            name.endsWith(".cr2"));
                }
            });

            Log.d("FILES", "Imatges a la carpeta: " + (files != null ? files.length : "null"));
            for(File f : files) {
                Log.d("FILES", (f.isDirectory() ?
                                "Directory: " :
                                "File: "
                                ) +
                                f.getName()
                );
            }
        }

        public int getCount() {
            return files != null ?
                    files.length :
                    0;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            RelativeLayout rl;

            if (convertView == null) {
                // if it's not recycled, initialize some attributes
 /*               imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(5, 5, 5, 5);*/

                rl = new RelativeLayout(mContext);
                ImageView i = new ImageView(mContext);
                i.setLayoutParams(new GridView.LayoutParams(300, 300));
                i.setScaleType(ImageView.ScaleType.CENTER_CROP);
                i.setPadding(5, 5, 5, 5);

                ImageView ck = new ImageView(mContext);
                ck.setLayoutParams(new GridView.LayoutParams(300, 300));
                ck.setPadding(5, 5, 5, 5);
                ck.setImageResource(R.drawable.completed);
                ck.setVisibility(View.INVISIBLE);

                Drawable d = Drawable.createFromPath(files[position].getAbsolutePath());
                i.setImageDrawable(d);
                rl.addView(i); // 0 imatge del fitxer
                rl.addView(ck); // 1 imatge de check

            } else {
                //imageView = (ImageView) convertView;
                rl = (RelativeLayout) convertView;
            }


            //Drawable d = Drawable.createFromPath(files[position].getAbsolutePath());
            //i.setImageDrawable(d);

            //return imageView;
            return rl;
        }


    }


}

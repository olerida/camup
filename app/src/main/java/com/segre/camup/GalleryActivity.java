package com.segre.camup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
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

public class GalleryActivity extends AppCompatActivity {

    ArrayList<Integer> okfoto = new ArrayList<Integer>();
    Integer[] fotook;
    public String path;
    File[] files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.send_fbtn);
        final Context c = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Pujar", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(c, "up up", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });
*/

        final GridView gridview = (GridView) findViewById(R.id.gallery_view);
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/" + PreferenceManager.getDefaultSharedPreferences(this).getString("local_path", "/Camera");

        gridview.setAdapter(new GalleryActivity.ImageAdapter(this, path));
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
                    Log.d("FOTOS", "ArrayList"+ okfoto.get(x));
                }
                fotook = okfoto.toArray(new Integer[okfoto.size()]);
                for(int x=0;x<fotook.length;x++) {
                    Log.d("FOTOS", "Array "+ fotook[x].toString());
                }

                //Toast.makeText(GalleryActivity.this, "" + position, Toast.LENGTH_SHORT).show();

            }

        });


    }

    private void deleteFile(File f) {
            Log.d("FILES", "Borrar " + f.getName());
            if (f.delete()) {
                Log.d("FILES", "Delete: " + f.getName());
            } else {
                Log.w("FILES", "Cant delete: " + f.getName());
            }
    }

    public void eliminarFotos(View view) {
        //Borrar Imatges seleccionades
        if (okfoto.size() > 0) {
            for(int okfile : okfoto) deleteFile(files[okfile]);
            Toast.makeText(this, "Imatges esborrades", Toast.LENGTH_SHORT).show();
            finish();
            Log.d("FILES", "Elements de l'Array seleccionada:" + okfoto.size());
            startActivity(getIntent());
        } else {
            final AlertDialog alertDialog = new AlertDialog.Builder(GalleryActivity.this).create();
            alertDialog.setTitle("Esborrar totes les imatges");
            alertDialog.setMessage("Segur que vols esborrar totes les imatges ?");
            final Context c = this;
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Confirmar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            for (File f : files) deleteFile(f);
                            Toast.makeText(c, "Totes les imatges esborrades", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(getIntent());
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Toast.makeText(c, "Operació cancel·lada", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(getIntent());
                        }
                    });
            alertDialog.show();
        }
    }

    public void refreshClick(View view) {
        finish();
        startActivity(getIntent());
    }

    public void upfotosClick(View view) {
        finish();
        startActivity(new Intent(this, UploadActivity.class));
    }


    public class ImageAdapter extends BaseAdapter {

        private Context mContext;

        public ImageAdapter(Context c, String path) {
            Log.d("FILES", "Images Path: " + path);
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
                rl = new RelativeLayout(mContext);
                ImageView i = new ImageView(mContext);
                i.setLayoutParams(new GridView.LayoutParams(300, 300));
                i.setScaleType(ImageView.ScaleType.CENTER_CROP);
                i.setPadding(5, 5, 5, 5);

                ImageView ck = new ImageView(mContext);
                ck.setLayoutParams(new GridView.LayoutParams(100, 100));
                ck.setPadding(0, 0, 0, 0);
                ck.setImageResource(R.drawable.completed);
                ck.setVisibility(View.INVISIBLE);

                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                ck.setLayoutParams(rlp);

                BitmapFactory.Options bopt = new BitmapFactory.Options();
                bopt.inSampleSize = 8;
                Bitmap b = BitmapFactory.decodeFile(files[position].getAbsolutePath(), bopt);
                Drawable d = new BitmapDrawable(getResources(), b);
                //Drawable d = Drawable.createFromPath(files[position].getAbsolutePath());
                i.setImageDrawable(d);
                rl.addView(i); // 0 imatge del fitxer
                rl.addView(ck); // 1 imatge de check

            } else rl = (RelativeLayout) convertView;

            return rl;
        }
    }
}

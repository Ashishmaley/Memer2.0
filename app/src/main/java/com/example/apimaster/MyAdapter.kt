package com.example.apimaster

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.net.wifi.p2p.WifiP2pManager.NetworkInfoListener
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.apimaster.databinding.ItemBinding
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Suppress("DEPRECATION")
class MyAdapter(private val context: Activity, private val arr: List<Meme>) :
    RecyclerView.Adapter<MyAdapter.MyViewholder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
        val view = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
        return MyViewholder(view, arr, context)
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        val currentItem = arr[position]
        val count = currentItem.preview.size - 1
        Picasso.get().load(currentItem.preview[count]).into(holder.binding.image)
        holder.binding.endLine
        val string = currentItem.ups.toString()
        holder.binding.title?.text = "\uD83D\uDC4D $string"
        holder.binding.save
        holder.binding.share
    }

    private fun shareImage(imageFile: File) {
        if (!imageFile.exists()) {
            Toast.makeText(context, "Image file does not exist", Toast.LENGTH_SHORT).show()
            return
        }

        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        if (bitmap == null) {
            Toast.makeText(context, "Failed to decode image", Toast.LENGTH_SHORT).show()
            return
        }

        // Convert the bitmap to JPEG format
        val jpegFile = File(imageFile.parent, "${imageFile.nameWithoutExtension}.jpg")
        val outputStream = FileOutputStream(jpegFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()

        val uri = FileProvider.getUriForFile(context, "com.example.apimaster.fileprovider", jpegFile)

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/jpeg"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }



    private fun saveImageToGallery(imageUrl: String) {
        val picasso = Picasso.get()

        val displayName = "${System.currentTimeMillis()}.jpg"

        // Define the destination directory
        val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val saveDir = File(imageDir, "MemeAppImages") // Custom folder name
        saveDir.mkdirs()

        val imageFile = File(saveDir, displayName)

        picasso.load(imageUrl).into(object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                try {
                    val outputStream = FileOutputStream(imageFile)
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()

                    // Notify the media scanner about the new image
                    val mediaScanIntent =
                        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile))
                    context.sendBroadcast(mediaScanIntent)

                    Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                // You can optionally handle the placeholder image preparation here
            }
        })
    }


    inner class MyViewholder(itemView: View, arr: List<Meme>, context: Activity) :
        RecyclerView.ViewHolder(itemView) {
        val binding: ItemBinding = ItemBinding.bind(itemView)

        init {
            binding.save?.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentItem = arr[position]
                    val imageUrl = currentItem.preview.last()
                    saveImageToGallery(imageUrl)
                }
            }

            binding.share?.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentItem = arr[position]
                    val imageUrl = currentItem.preview.last()

                    val picasso = Picasso.get()

                    val displayName = "${System.currentTimeMillis()}.jpg"

                    // Define the destination directory
                    val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val saveDir = File(imageDir, "MemeAppImages") // Custom folder name
                    saveDir.mkdirs()

                    val imageFile = File(saveDir, displayName)

                    picasso.load(imageUrl).into(object : com.squareup.picasso.Target {
                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            try {
                                val outputStream = FileOutputStream(imageFile)
                                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                                outputStream.flush()
                                outputStream.close()

                                // Notify the media scanner about the new image
                                val mediaScanIntent =
                                    Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile))
                                context.sendBroadcast(mediaScanIntent)

                                // Share the image
                                shareImage(imageFile)
                            } catch (e: IOException) {
                                e.printStackTrace()
                                Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                            Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
                        }

                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                            // You can optionally handle the placeholder image preparation here
                        }
                    })
                }
            }
        }
    }
}
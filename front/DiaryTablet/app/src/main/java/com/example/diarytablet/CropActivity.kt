import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.CropImageView
import com.example.diarytablet.R

class CropActivity : AppCompatActivity() {
    private lateinit var cropImageView: CropImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.canhub.cropper.R.layout.crop_image_activity)

        cropImageView = findViewById(R.id.cropImageView)

        val uri = intent.getParcelableExtra<Uri>("imageUri")
        uri?.let {
            cropImageView.setImageUriAsync(it)
        }

        cropImageView.setOnCropImageCompleteListener { _, result ->
            val croppedUri = result.uriContent
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra("croppedImageUri", croppedUri.toString())
            })
            finish()
        }
    }
}

package s.maciej.scrandroid.utils

import android.util.Log
import com.google.firebase.database.DatabaseReference
import s.maciej.scrandroid.data.ModelData
import java.text.SimpleDateFormat
import java.util.*


class Firebase {

    companion object {
        fun addData(firebaseData: DatabaseReference) {
            val models: MutableList<ModelData?> = mutableListOf()

            for (i in 0..15) {
                models.add(ModelData(0, 3, 3, 0, "","","",false))
            }

            models.forEach {
                val key = firebaseData.child("Queue").push().key
                it!!.uuid = key

                firebaseData.child("Queue").child(key).setValue(it)
            }

        }

        fun addToCompleted(databaseReference: DatabaseReference, model: ModelData) {
            try {
                Log.i("Model", "DODAJE DO COMPLETED")

                databaseReference.child("Completed")
                        .child(model.uuid)
                        .setValue(model)
            } catch (e: Exception) {
                Log.i("Model", "PUSHING ERROR")
            }
        }


        fun updateObject(databaseReference: DatabaseReference, model: ModelData) {
            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("HH:mm:ss")
            model.time = sdf.format(cal.time)
            try {
                databaseReference.child("Queue")
                        .child(model.uuid)
                        .setValue(model)
            } catch (e: Exception) {
                Log.i("Model", "UPDATEERROR ERROR")
            }

        }

        fun deleteObjectFromQueue(databaseReference: DatabaseReference, uuid: String) {
            try {
                Log.i("Model", "USUWAM ")

                databaseReference.child("Queue")
                        .child(uuid)
                        .removeValue()
            } catch (e: Exception) {
                Log.i("Model", "USUWANIE ERROR")

            }


        }
    }

}
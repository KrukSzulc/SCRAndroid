package s.maciej.scrandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.google.firebase.database.*
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import s.maciej.scrandroid.data.ModelData
import s.maciej.scrandroid.utils.Firebase
import java.util.*


class MainActivity : AppCompatActivity() {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val list: MutableList<ModelData> = mutableListOf()

    private var button: TextView? = null

    var currentUuid: String? = "X"
    var currId: String? = ""

    var licznik: Int = 0

    private var menuListener: ValueEventListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        button = findViewById(R.id.idButton)

        buton()
        downloadFromDb()
    }

    fun buton(): Disposable {
        return RxView.clicks(button!!).share()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe { t -> clicked() }
    }

    fun clicked() {
        Firebase.addData(databaseReference)
    }

    fun tryUpdate(model: ModelData) {
        databaseReference.child("Queue").removeEventListener(menuListener)

        Log.i("Model", "WYSYLAM  " + model.specialUuid)
        Firebase.updateObject(databaseReference, model)
        Thread.sleep(500)

        databaseReference.child("Queue").addValueEventListener(menuListener)
    }

    fun makeTask(model: ModelData) {
        Log.i("Model", "TAK, TO MOJE ZADANIE")

        databaseReference.child("Queue").removeEventListener(menuListener)
        licznik++

        Firebase.updateObject(databaseReference, model)

        Thread.sleep(1000)
        Firebase.deleteObjectFromQueue(databaseReference, model.uuid)
        Firebase.addToCompleted(databaseReference, model)
       // Thread.sleep(500)
        databaseReference.child("Queue").addValueEventListener(menuListener)


        Log.i("Model", "KONCZE TASK")
    }



    fun downloadFromDb() {
        menuListener = object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot?) {
                list.clear()
                p0!!.children.mapNotNullTo(list) { it.getValue<ModelData>(ModelData::class.java) }

                if ( list.count{it.blocked == false} >0) {

                    var model: ModelData = list.first{ !it.blocked }

                    Log.i("Model", "AKTUALNE ID ${model.specialUuid}")

                    if (model.specialUuid == currentUuid!!) {
                        currentUuid = "clear"
                        model.blocked = true
                        makeTask(model)
                    } else {
                        model = list.first{it.specialUuid == ""}
                        currentUuid = UUID.randomUUID().toString()
                        model.specialUuid = currentUuid as String
                        tryUpdate(model)
                    }
                } else Log.i("Model", "CZEKAM + $licznik")

            }
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        databaseReference.child("Queue").addValueEventListener(menuListener)


    }

}


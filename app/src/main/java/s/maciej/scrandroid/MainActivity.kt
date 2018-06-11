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
import s.maciej.scrandroid.utils.calculate
import java.util.*


class MainActivity : AppCompatActivity() {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val list: MutableList<ModelData> = mutableListOf()

    private var button: TextView? = null

    var currentUuid: String? = "XXXX"

    var licznik: Int = 0

    private var menuListener: ValueEventListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        button = findViewById(R.id.idButton)

        buton()
        downloadFromDb()

        button!!.text = licznik.toString()

    }

    fun buton(): Disposable {
        return RxView.clicks(button!!).share()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe { t -> clicked() }
    }

    fun clicked() {
       // Firebase.addData(databaseReference)
        licznik = 0
        button!!.text = licznik.toString()
    }

    fun tryUpdate(model: ModelData) {
        databaseReference.child("Queue").removeEventListener(menuListener)

        Firebase.updateObject(databaseReference, model)
        Thread.sleep(250)

        databaseReference.child("Queue").addValueEventListener(menuListener)
    }

    fun makeTask(model: ModelData) {
        Log.i("Model", "TAK, TO MOJE ZADANIE")

        databaseReference.child("Queue").removeEventListener(menuListener)
        licznik++

        Firebase.updateObject(databaseReference, model)

        Thread.sleep(200)
        button!!.text = licznik.toString()

        model.content = calculate(model.a!!, model.b!!)

        Firebase.deleteObjectFromQueue(databaseReference, model.uuid)
        Firebase.addToCompleted(databaseReference, model)

        databaseReference.child("Queue").addValueEventListener(menuListener)
        Log.i("Model", "KONCZE TASK")
    }


    fun downloadFromDb() {
        menuListener = object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot?) {

                    list.clear()
                    p0!!.children.mapNotNullTo(list) { it.getValue<ModelData>(ModelData::class.java) }

                    if (list.count { it.blocked == false && it.uuid != "ss" } > 0) {

                        var model: ModelData = list.first { !it.blocked && it.uuid != "ss" }

                        Log.i("Model", "AKTUALNE ID ${model.specialUuid}")

                        if (model.specialUuid == currentUuid!!) {
                            currentUuid = "clear"
                            model.blocked = true
                            makeTask(model)

                        } else if (model.specialUuid == "") {
                            Log.i("Model", "PROBUJE PRZYPISAC")
                            currentUuid = UUID.randomUUID().toString()
                            model.specialUuid = currentUuid as String
                            tryUpdate(model)
                        } else if (model.specialUuid != "" && model.blocked == false) {
                            Log.i("Model", "WYJATEK CZEKAM")
                           /* model.blocked = false
                            model.specialUuid = ""
                            tryUpdate(model)*/
                        }

                    } else Log.i("Model", "CZEKAM + $licznik")


            }

            override fun onCancelled(p0: DatabaseError?) {
            }
        }

        databaseReference.child("Queue").addValueEventListener(menuListener)


    }

}


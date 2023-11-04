package com.example.firebase_test

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.firebase_test.databinding.ActivityCalendar2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import org.checkerframework.checker.nullness.qual.NonNull
import java.util.*
import kotlin.collections.ArrayList

class Calendar2 : AppCompatActivity() {

    lateinit var binding: ActivityCalendar2Binding
    private val fireStore = FirebaseFirestore.getInstance()
    private var uId : String? = null
    private var calendarDTO : CalendarDTO? = null
    private var matchDTO : MatchDTO? = null
    var otherUid : String? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendar2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        otherUid = intent.getStringExtra("otheruid")

        binding.calendarView.setSelectedDate(CalendarDay.today())

        uId = FirebaseAuth.getInstance().currentUser?.uid

/*        fireStore.collection("calendar").document(uId!!).collection("calendar").document("20230611")
            .addSnapshotListener { documentSnapshot, _ ->
                if (documentSnapshot == null) return@addSnapshotListener    //데이터가없다면
                var calData : String? = null
                Log.d("로그","CalendarDay.today(): "+CalendarDay.today())

                calendarDTO = documentSnapshot.toObject(CalendarDTO::class.java)
                Log.d("로그","calendarDTO3: "+calendarDTO)

                if (calendarDTO != null){
                    calData = calendarDTO!!.health.toString()
                }
                val cal = CalendarDay.from(2023,6,10)
                val calList = ArrayList<CalendarDay>()
                //calList.add(CalendarDay{2023-6-11})



                binding.calendarView.setOnDateChangedListener { widget, date, selected ->
                    val calList = ArrayList<CalendarDay>()
                    calList.add(date)
                    Log.d("로그","date: "+date.year + " " + +date.month + " " + +date.day)

                    binding.textView.text = calData

                }

            }*/

        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            val calList = ArrayList<CalendarDay>()
            calList.add(date)
            Log.d("로그","date: "+date.year + " " + +date.month + " " + +date.day)
            val y = date.year
            var m : String? = null
            var d : String? = null

            if(date.month<10)
                m = "0"+date.month
            else
                m = ""+date.month
            if(date.day<10)
                d = "0"+date.day
            else
                d = ""+date.day


            fireStore.collection("calendar").document(uId!!).collection("calendar").document(y.toString()+m+d)
                .addSnapshotListener { documentSnapshot, _ ->
                    if (documentSnapshot == null) return@addSnapshotListener    //데이터가없다면
                    var calData : String? = null
                    var calData_time : String? = null
                    Log.d("로그","CalendarDay.today(): "+CalendarDay.today())

                    calendarDTO = documentSnapshot.toObject(CalendarDTO::class.java)
                    Log.d("로그","calendarDTO3: "+calendarDTO)

                    if (calendarDTO != null){
                        calData = calendarDTO!!.health.toString()
                        calData_time = calendarDTO!!.time.toString()
                    }
                    val cal = CalendarDay.from(2023,6,10)
                    val calList = ArrayList<CalendarDay>()
                    //calList.add(CalendarDay{2023-6-11})

                    binding.textView.text = calData
                    binding.timetext.text = calData_time
                }
            //상대방 id찾기
            fireStore.collection("match").document(uId!!)
                .addSnapshotListener { documentSnapshot, _ ->
                    if (documentSnapshot == null) return@addSnapshotListener    //데이터가없다면


                    matchDTO = documentSnapshot.toObject(MatchDTO::class.java)
                    Log.d("로그","matchDTO: "+matchDTO)
                    //상대가 있다면
                    if (matchDTO != null){
                        otherUid = matchDTO!!.uid.toString()
                        Log.d("로그","otherUid2: "+ otherUid)

                        //상대 DB에서 운동완료 정보를 찾아옴
                        fireStore.collection("calendar").document(otherUid!!).collection("calendar").document(y.toString()+m+d)
                            .addSnapshotListener { documentSnapshot, _ ->
                                if (documentSnapshot == null) return@addSnapshotListener    //데이터가없다면
                                var calData : String? = null
                                var calData_time : String? = null
                                Log.d("로그","CalendarDay.today(): "+CalendarDay.today())

                                calendarDTO = documentSnapshot.toObject(CalendarDTO::class.java)
                                Log.d("로그","calendarDTO3: "+calendarDTO)

                                if (calendarDTO != null){
                                    calData = calendarDTO!!.health.toString()
                                    calData_time = calendarDTO!!.time.toString()
                                }
                                val cal = CalendarDay.from(2023,6,10)
                                val calList = ArrayList<CalendarDay>()
                                //calList.add(CalendarDay{2023-6-11})

                                binding.textView2.text = calData
                                binding.timetext2.text = calData_time
                            }
                    }


                }




        }

        //상대가 있다면 한명만 운동완료시 주황색, 둘다 완료시 초록색
        //상대가 없다면 자신만 완료시 초록색
        val myCom = mutableListOf<String>()
        var otherCom = mutableListOf<String>()

        var resultDTOs: ArrayList<CalendarDTO> = arrayListOf()
        fireStore.collection("calendar").document(uId!!).collection("calendar")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            resultDTOs.clear()
            if (querySnapshot == null) {
                return@addSnapshotListener
            }

            // 데이터 받아오기
            for (snapshot in querySnapshot!!.documents) {
                var item = snapshot.toObject(CalendarDTO::class.java)
                resultDTOs.add(item!!)
                Log.d("로그","resultDTOs: "+resultDTOs)

                if(item.health == "완료"){
                    var tk1 = item.day!!.chunked(4);
                    var tk2 = tk1[1].chunked(2);
                    myCom.add(item.day.toString())
                    Log.d("로그","item.day: "+tk1[0]+" "+tk2[0] + " " + tk2[1])
                    val cal = CalendarDay.from(tk1[0].toInt(),tk2[0].toInt(),tk2[1].toInt())
                    //운동완료시 표시
                    binding.calendarView.addDecorator(EventDecorator(Color.RED, Collections.singleton(cal) )  )
                }

            }
            //상대
            fireStore.collection("calendar").document(otherUid!!).collection("calendar")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                resultDTOs.clear()
                if (querySnapshot == null) {
                    return@addSnapshotListener
                }

                // 데이터 받아오기
                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(CalendarDTO::class.java)
                    resultDTOs.add(item!!)
                    Log.d("로그","resultDTOs: "+resultDTOs)

                    if(item.health == "완료"){
                        var tk1 = item.day!!.chunked(4)
                        var tk2 = tk1[1].chunked(2)
                        otherCom.add(item.day.toString())
                        Log.d("로그","myCom2123: "+myCom)
                        Log.d("로그","otherCom2123: "+otherCom)
                        Log.d("로그","item.day: "+tk1[0]+" "+tk2[0] + " " + tk2[1])
                        val cal = CalendarDay.from(tk1[0].toInt(),tk2[0].toInt(),tk2[1].toInt())
                        //운동완료시 표시
                        binding.calendarView.addDecorator(EventDecorator(Color.RED, Collections.singleton(cal) )  )
                    }

                }
                Log.d("로그","myCom3123: "+myCom)
                Log.d("로그","otherCom3123: "+otherCom)

                for(i in myCom){
                    val r = otherCom.find { it==i }
                    if(r!=null) {
                        Log.d("로그", "rrrrr: " + r)
                        var tk1 = r.chunked(4)
                        var tk2 = tk1[1].chunked(2)
                        val cal = CalendarDay.from(tk1[0].toInt(),tk2[0].toInt(),tk2[1].toInt())
                        binding.calendarView.addDecorator(EventDecorator(Color.GREEN, Collections.singleton(cal) )  )
                    }

                }
            }
            Log.d("로그","myCom1123: "+myCom)
            Log.d("로그","otherCom1123: "+otherCom)

        }



        //binding.calendarView.addDecorator(EventDecorator(Color.RED, Collections.singleton(CalendarDay.today() ) )  )




    }


}
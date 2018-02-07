package tests


import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import org.junit.Test

import org.junit.Assert.assertEquals

/**
 * Created by andrei.rybin on 1/31/18.
 */

class FlowableTest {

    @Test
    fun testFlowableFlow() {

        var expectedResult = 0

        val calculator = FlowableCalculator()
        val disposable = calculator.flowable
                .subscribeOn(Schedulers.io())
                .onBackpressureLatest()
                .observeOn(Schedulers.computation())
                .subscribe({
                    assertEquals(expectedResult, it)
                    expectedResult++

                    println("Posted result $it")
                })

        try{
            Thread.sleep(150)
        } catch (e: InterruptedException) {
            //Make sure the threading pool is all setup
        }

        calculator.postToFlowable(FlowableWithDelay(0))
        calculator.postToFlowable(FlowableWithDelay(1))
        calculator.postToFlowable(RegularFlowable(2))
        calculator.postToFlowable(RegularFlowable(3))
        calculator.postToFlowable(RegularFlowable(4))
        calculator.postToFlowable(FlowableWithDelay(5))
        calculator.postToFlowable(FlowableWithDelay(6))
        disposable.dispose()
        calculator.postToFlowable(RegularFlowable(7))
        calculator.postToFlowable(FlowableWithDelay(8))
        calculator.postToFlowable(FlowableWithDelay(9))

    }


    inner class FlowableWithDelay(private val value: Int) : SomeFlowablePost {

        override fun postResult(): Int {
            try {
                Thread.sleep(50)
            } catch (e : InterruptedException) {
                //oh oh
            }
            return value
        }

    }

    inner class RegularFlowable(private val value: Int) : SomeFlowablePost {
        override fun postResult(): Int {
            return value
        }

    }

    inner class FlowableCalculator {
        private val publisher = PublishProcessor.create<SomeFlowablePost>()
        val flowable: Flowable<Int> = Flowable.fromPublisher(publisher)
                .flatMap {
                    Flowable.fromCallable { it.postResult() }
                }.subscribeOn(Schedulers.newThread())

        fun postToFlowable(post: SomeFlowablePost) {
            publisher.onNext(post)
        }
    }

    interface SomeFlowablePost {
        fun postResult() : Int
    }

}

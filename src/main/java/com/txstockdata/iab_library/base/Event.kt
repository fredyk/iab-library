package com.txstockdata.iab_library.base

/**
 * Created by Jhon Fredy Magdalena Vila on 10/12/2020.
 */
open interface Event<Communicator> {
    fun onError()
    fun onError(var1: Communicator)
    fun onPostExecute()
    fun onSuccess()
    fun onSuccess(var1: Communicator)
}

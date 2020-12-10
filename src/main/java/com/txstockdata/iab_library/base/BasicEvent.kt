package com.txstockdata.iab_library.base

open class BasicEvent<Communicator> : Event<Communicator> {
    private var calledPost = false
    private var communicatorBasicEvent: Event<Communicator>? = null

    constructor(communicator: Event<Communicator>?) {
        communicatorBasicEvent = communicator
    }

    constructor() {}

    override fun onError() {
        if (!calledPost) {
            onPostExecute()
            if (communicatorBasicEvent != null) {
                communicatorBasicEvent!!.onError()
            }
            calledPost = true
        }
    }

    override fun onError(communicator: Communicator) {
        if (communicatorBasicEvent != null) {
            communicatorBasicEvent!!.onError(communicator)
        }
        this.onError()
    }

    override fun onPostExecute() {
        if (communicatorBasicEvent != null) {
            communicatorBasicEvent!!.onPostExecute()
        }
    }

    override fun onSuccess() {
        if (!calledPost) {
            if (communicatorBasicEvent != null) {
                communicatorBasicEvent!!.onSuccess()
            }
            onPostExecute()
            calledPost = true
        }
    }

    override fun onSuccess(communicator: Communicator) {
        if (communicatorBasicEvent != null) {
            communicatorBasicEvent!!.onSuccess(communicator)
        }
        this.onSuccess()
    }
}
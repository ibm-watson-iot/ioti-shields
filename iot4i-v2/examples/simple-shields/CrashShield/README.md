### Crash Shield


This shield generate hazards whenever a car crash event is received from the [Bosch retrofit sensor](http://www.bosch-connectivity.com/what_we_offer/products_and_solutions_1/retrofit_ecall/connected_devices_3).


The main spl code of this shield is [here](./com.ibm.iot4i.examples/CrashShield.spl).

This shield follows the [shield template](../../../shield-templates).


The only difference from the shield template is the shield specific operator which is shown below:

```
//  Shield Specific Operators goes here
		@view(name = "Hazards", port = HazardStream, sampleSize = 5, bufferSize = 50)
		@parallel(width = 1)
		(stream<Hazard> HazardStream) as shield = Functor(ValidatedStream)
		{
			param
				filter : checkCrash(message) ;
			output
				HazardStream : userId = userId, rawEvents = [ message ], actionParams =
					"{\"hazardTitle\":\"Crash Detected\",\"emailSubject\":\"Alert from IoT for Insurance\",\"emailText\":\"Hello IoT for Insurance user. You have a hazard!!!\" }" ;
		}

```

The above operator filter the events based on the [java function "checkCrash"](./impl/java/src/com/ibm/iot4i/examples/CheckCrashImpl.java). Then it prepares the hazard and pass it to the sink operator.


Additionally this shield uses an [EntryCondition](./impl/java/src/com/ibm/iot4i/examples/EntryConditionImpl.java) function to filter the events at the beginning of the shield as shown below:

```
//  filter events based on entry condition
		@view(name = "FilteredEvents", port = FilteredStream, sampleSize = 5,
			bufferSize = 50)
		@parallel(width = 1)
		(stream<rstring userId, rstring key, rstring message> FilteredStream) as
			filter = Filter(SourceStream)
		{
			param
				filter : entryCondition(message) ;
		}
```
 
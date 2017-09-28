#### Repo structure

* [Iot4i Shield Toolkit](./iot4i-shield-toolkit):

This toolkit contains all the operators, functions and dependencies that you would need to develop shields.

* [Shield Templates](./shield-templates):

A shield template that simplify the shield development. It shows the main structure of a shield including all required stages. 

* [Examples](./examples):

A set of examples to show how to use the shield template to develop different type of shields. This is divided into two categories:
 
1. [Simple Shields](./examples/simple-shields) 
2. [Complex Shields](./examples/complex-shields)

## Developer Guide


### Prerequisites
 
Before you begin, ensure that the following prerequisites are in place:

- Download [IBM Streams Quick Start Edition (QSE) VM ](https://www-01.ibm.com/marketing/iwm/iwm/web/preLogin.do?source=swg-ibmistvi&S_PKG=ov14959&S_TACT=000000VP&S_OFF_CD=10000737).

### Setting up your environment

- Setup the IBM Streams QSE VM. Please refer to the [streaming analytics development guide](https://developer.ibm.com/streamsdev/docs/bluemix-streaming-analytics-development-guide/).
- Add the streamsx.kafka toolkit
  * Download the [latest release of the toolkit](https://github.com/IBMStreams/streamsx.kafka/releases).
  * Extract the archived (.tgz) file.
  * From the "Streams Explorer" tab choose "Add Toolkit Location".
  * Select the path to the root directory of the streamsx.kafka then click OK.
- Add the iot4i-shield-toolkit:
  * Clone or Download this repo.
  * From the "Streams Explorer" tab choose "Add Toolkit Location".
  * Select the path to the root directory of the [iot4i-shield-toolkit](./iot4i-shield-toolkit) then click OK.


After setting the IBM streams environment and adding the dependent toolkits. You can start developing shields based on the [shield template](./shield-templates).

For an example of a simple shield, please refer to the [Queryable Simple Shield](./examples/simple-shields/QueryableSimpleShield).
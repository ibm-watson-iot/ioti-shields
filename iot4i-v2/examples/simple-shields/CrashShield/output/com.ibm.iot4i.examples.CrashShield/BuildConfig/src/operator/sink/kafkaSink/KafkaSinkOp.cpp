// eJzNVV1vmzAUlf9JFO0BtgjzkWStVVXqmkVKl6aV0j4jA15wARvZTin79XMMjZLMD_0nDtAnExYeLz7nX19cprzyaVJ5UguBKvnkF_1llghH7szKPg2TYlAgTA11cIhp4Hu1u1NYHfyF0j7tfjcJkWr_033ZruSkwexbsJfvq_0WxeT_0qXm_0wevFYqa8fAiu1o_1L_0G61mG9ZqihnMk55FWv2mHI1prtRxZmXX2uuIAIlVZ98oP9BSGh5lG2AwQILFlqwyIKNLdjEgk0t2FcLdmHBLm2arYHYIglOQwEw5xWB_1QLhrKIMNlwUssYpgbcCy3ydU1JmMMMKA8YZ8UEE0hJLucIVAScsx0qcYWqtAI_1XRGDFhfSOSuGhh4cuuFF6hmSryFwQ0vEtaSKwaEF0SumY8X7hEXpWVEfbIrQh6onzsqBqRgVJ9dStc47AoesOvgyOPSGt6hK_04FcME8q0x4nGfyOK1wpmvGElxxnJ4Of_1RZdJVkkTm6Ja7NZfUSLntPyjhAKAkAEONzNCmhshTa4fu_11sAL2hjW987KoDeS_0rA55duX4seNMj3NHA7JzOhJ2JRgPzdawD79XKuoxTLNXVYTCjLi1bytR0PLjWorWH0_19hk_1HCk8XMcV3N6jiGYvrXKPTEpkO55zFogig8h0Cn8jbHjJGyC8S0t85cdOayz6nf2z65QXhaKbujgdZc7Hv1Bw4FQIEwJwvQHUPxmqbgvcQK0u7fKyIl3pD92P8Ns2gyBw


#include "./KafkaSinkOp.h"
using namespace SPL::_Operator::sink::kafkaSink;

#include <SPL/Runtime/Function/SPLFunctions.h>
#include <SPL/Runtime/Operator/Port/Punctuation.h>


#define MY_OPERATOR_SCOPE SPL::_Operator::sink::kafkaSink
#define MY_BASE_OPERATOR KafkaSinkOp_Base
#define MY_OPERATOR KafkaSinkOp$OP





#include <dlfcn.h>

#include <SPL/Toolkit/JavaOp.h>
#include <SPL/Toolkit/RuntimeException.h>

MY_OPERATOR_SCOPE::MY_OPERATOR::MY_OPERATOR()
{
  /* Get a handle to the Java virtual machine */
  SPL::JNI::JVMControl *jvmControl = SPL::JNI::JVMControl::getJVM();
   
  /* Attach to the JVM  for the duration of the initialization */
  JNIEnv * env = jvmControl->attach();

  /* How we invoke methods against an OperatorSetup instance */
  SPL::JNI::OpSetupInvoker* osi = jvmControl->getOpSetupInvoker();

  /* OperatorSetup instance specific to this operator */
  jobject setup = osi->newSetup(env, this); 

  /**
     Pass the parameters into my OperatorSetup instance.
  */
     osi->setParameter(env, setup, "className", SPL::rstring("com.ibm.streamsx.kafka.operators.KafkaProducerOperator"));
   osi->setParameter(env, setup, "classLibrary", (SPL::Functions::Utility::getToolkitDirectory(SPL::rstring("com.ibm.streamsx.kafka")) + SPL::rstring("/impl/java/bin")));
   osi->setParameter(env, setup, "classLibrary", (SPL::Functions::Utility::getToolkitDirectory(SPL::rstring("com.ibm.streamsx.kafka")) + SPL::rstring("/opt/downloaded/*")));
   osi->setParameter(env, setup, "classLibrary", (SPL::Functions::Utility::getToolkitDirectory(SPL::rstring("com.ibm.streamsx.kafka")) + SPL::rstring("/impl/lib/*")));
   osi->setParameter(env, setup, "propertiesFile", ::SPL::JNIFunctions::com::ibm::iot4i::common::SPL_JNIFunctions::getKafkaPropertiesFileName(SPL::Functions::Utility::getToolkitDirectory(lit$0), lit$1, lit$2, lit$3, (lit$4 + ::SPL::spl_cast<SPL::rstring, SPL::uint64 >::cast(::SPL::Functions::Utility::jobID())), (((lit$6 + ::SPL::spl_cast<SPL::rstring, SPL::uint64 >::cast(::SPL::Functions::Utility::jobID())) + lit$5) + ::SPL::spl_cast<SPL::rstring, SPL::int32 >::cast(::SPL::Functions::Utility::getChannel())), lit$7, lit$8, lit$9, lit$10, lit$11, lit$12));


  /**
    Pass input port information into the Java operator.
 
    Are logic clauses present.
   
    Pass the windowing information for each port as
    a list of values for the parameter '[window].N' where
    N is the index of the windowed input port.
  */
     osi->setParameter(env, setup, "[inputPortLogic].0", "none");
 hasTupleLogic = false;


  
  /* At this point all the initialization information has been
     passed to OperatorSetup. Create a JNIBridge instance object
     we use to communicate with the user's Operator implementation
     at runtime.
  */
  
  _bi = jvmControl->getBridgeInvoker();
  _bridge = _bi->newBridge(env, this, setup);
        
  /* Completed Java initialization, detach from the JVM */
  jvmControl->detach();

  setupStateHandler();

  void * handle = dlopen("libstreams-stdtk-javaop.so", RTLD_LAZY);
  if (!handle) {
    const FormattableMessage& m = SPL_APPLICATION_RUNTIME_EXCEPTION("libstreams-stdtk-javaop.so");
    THROW_STRING(SPLRuntimeJavaOperator, m);
  }
  _fp = (FP) dlsym(handle, "callProcessTupleWithNativeByteBuffer");
}

MY_OPERATOR_SCOPE::MY_OPERATOR::~MY_OPERATOR() 
{
}

void MY_OPERATOR_SCOPE::MY_OPERATOR::setupStateHandler()
{
    _stateHandler.reset(new SPL::JNI::JavaOpStateHandler(_bi, _bridge));
    getContext().registerStateHandler(*_stateHandler);
}

void MY_OPERATOR_SCOPE::MY_OPERATOR::allPortsReady() 
{
    _bi->allPortsReady(_bridge);
    createThreads(1);
}
 
void MY_OPERATOR_SCOPE::MY_OPERATOR::prepareToShutdown() 
{
   _bi->shutdown(_bridge);
}

void MY_OPERATOR_SCOPE::MY_OPERATOR::process(uint32_t idx)
{
   _bi->complete(_bridge);
}

void MY_OPERATOR_SCOPE::MY_OPERATOR::process(Tuple & tuple, uint32_t port)
{
}

void MY_OPERATOR_SCOPE::MY_OPERATOR::process(Tuple const & tuple, uint32_t port)
{
   _bi->processTuple(_bridge, tuple, port);
}

void MY_OPERATOR_SCOPE::MY_OPERATOR::process(Punctuation const & punct, uint32_t port)
{
   _bi->processPunctuation(_bridge, punct, port);
}

static SPL::Operator * initer() { return new MY_OPERATOR_SCOPE::MY_OPERATOR(); }
bool MY_BASE_OPERATOR::globalInit_ = MY_BASE_OPERATOR::globalIniter();
bool MY_BASE_OPERATOR::globalIniter() {
    instantiators_.insert(std::make_pair("sink::kafkaSink::KafkaSinkOp",&initer));
    return true;
}

template<class T> static void initRTC (SPL::Operator& o, T& v, const char * n) {
    SPL::ValueHandle vh = v;
    o.getContext().getRuntimeConstantValue(vh, n);
}

MY_BASE_OPERATOR::MY_BASE_OPERATOR()
 : Operator() {
    uint32_t index = getIndex();
    initRTC(*this, lit$0, "lit$0");
    initRTC(*this, lit$1, "lit$1");
    initRTC(*this, lit$2, "lit$2");
    initRTC(*this, lit$3, "lit$3");
    initRTC(*this, lit$4, "lit$4");
    initRTC(*this, lit$5, "lit$5");
    initRTC(*this, lit$6, "lit$6");
    initRTC(*this, lit$7, "lit$7");
    initRTC(*this, lit$8, "lit$8");
    initRTC(*this, lit$9, "lit$9");
    initRTC(*this, lit$10, "lit$10");
    initRTC(*this, lit$11, "lit$11");
    initRTC(*this, lit$12, "lit$12");
    param$className$0 = SPL::rstring("com.ibm.streamsx.kafka.operators.KafkaProducerOperator");
    addParameterValue ("className", SPL::ConstValueHandle(param$className$0));
    param$classLibrary$0 = (SPL::Functions::Utility::getToolkitDirectory(SPL::rstring("com.ibm.streamsx.kafka")) + SPL::rstring("/impl/java/bin"));
    addParameterValue ("classLibrary", SPL::ConstValueHandle(param$classLibrary$0));
    param$classLibrary$1 = (SPL::Functions::Utility::getToolkitDirectory(SPL::rstring("com.ibm.streamsx.kafka")) + SPL::rstring("/opt/downloaded/*"));
    addParameterValue ("classLibrary", SPL::ConstValueHandle(param$classLibrary$1));
    param$classLibrary$2 = (SPL::Functions::Utility::getToolkitDirectory(SPL::rstring("com.ibm.streamsx.kafka")) + SPL::rstring("/impl/lib/*"));
    addParameterValue ("classLibrary", SPL::ConstValueHandle(param$classLibrary$2));
    param$propertiesFile$0 = ::SPL::JNIFunctions::com::ibm::iot4i::common::SPL_JNIFunctions::getKafkaPropertiesFileName(SPL::Functions::Utility::getToolkitDirectory(lit$0), lit$1, lit$2, lit$3, (lit$4 + ::SPL::spl_cast<SPL::rstring, SPL::uint64 >::cast(::SPL::Functions::Utility::jobID())), (((lit$6 + ::SPL::spl_cast<SPL::rstring, SPL::uint64 >::cast(::SPL::Functions::Utility::jobID())) + lit$5) + ::SPL::spl_cast<SPL::rstring, SPL::int32 >::cast(::SPL::Functions::Utility::getChannel())), lit$7, lit$8, lit$9, lit$10, lit$11, lit$12);
    addParameterValue ("propertiesFile", SPL::ConstValueHandle(param$propertiesFile$0));
    (void) getParameters(); // ensure thread safety by initializing here
}
MY_BASE_OPERATOR::~MY_BASE_OPERATOR()
{
    for (ParameterMapType::const_iterator it = paramValues_.begin(); it != paramValues_.end(); it++) {
        const ParameterValueListType& pvl = it->second;
        for (ParameterValueListType::const_iterator it2 = pvl.begin(); it2 != pvl.end(); it2++) {
            delete *it2;
        }
    }
}

void MY_BASE_OPERATOR::tupleLogic(Tuple const & tuple, uint32_t port) {
}


void MY_BASE_OPERATOR::processRaw(Tuple const & tuple, uint32_t port) {
    tupleLogic (tuple, port);
    static_cast<MY_OPERATOR_SCOPE::MY_OPERATOR*>(this)->MY_OPERATOR::process(tuple, port);
}


void MY_BASE_OPERATOR::punctLogic(Punctuation const & punct, uint32_t port) {
}

void MY_BASE_OPERATOR::punctPermitProcessRaw(Punctuation const & punct, uint32_t port) {
    {
        punctNoPermitProcessRaw(punct, port);
    }
}

void MY_BASE_OPERATOR::punctNoPermitProcessRaw(Punctuation const & punct, uint32_t port) {
    switch(punct) {
    case Punctuation::WindowMarker:
        punctLogic(punct, port);
        process(punct, port);
        break;
    case Punctuation::FinalMarker:
        punctLogic(punct, port);
        process(punct, port);
        break;
    case Punctuation::DrainMarker:
    case Punctuation::ResetMarker:
    case Punctuation::ResumeMarker:
        break;
    case Punctuation::SwitchMarker:
        javaopInternalAction(punct, port);
        break;
    default:
        break;
    }
}

void MY_BASE_OPERATOR::processRaw(Punctuation const & punct, uint32_t port) {
    switch(port) {
    case 0:
        punctNoPermitProcessRaw(punct, port);
        break;
    }
}



void MY_BASE_OPERATOR::checkpointStateVariables(NetworkByteBuffer & opstate) const {
}

void MY_BASE_OPERATOR::restoreStateVariables(NetworkByteBuffer & opstate) {
}

void MY_BASE_OPERATOR::checkpointStateVariables(Checkpoint & ckpt) {
}

void MY_BASE_OPERATOR::resetStateVariables(Checkpoint & ckpt) {
}

void MY_BASE_OPERATOR::resetStateVariablesToInitialState() {
}

bool MY_BASE_OPERATOR::hasStateVariables() const {
    return false;
}

void MY_BASE_OPERATOR::resetToInitialStateRaw() {
    AutoMutex $apm($svMutex);
    StateHandler *sh = getContext().getStateHandler();
    if (sh != NULL) {
        sh->resetToInitialState();
    }
    resetStateVariablesToInitialState();
}

void MY_BASE_OPERATOR::checkpointRaw(Checkpoint & ckpt) {
    AutoMutex $apm($svMutex);
    StateHandler *sh = getContext().getStateHandler();
    if (sh != NULL) {
        sh->checkpoint(ckpt);
    }
    checkpointStateVariables(ckpt);
}

void MY_BASE_OPERATOR::resetRaw(Checkpoint & ckpt) {
    AutoMutex $apm($svMutex);
    StateHandler *sh = getContext().getStateHandler();
    if (sh != NULL) {
        sh->reset(ckpt);
    }
    resetStateVariables(ckpt);
}





// eJzNVV1vmzAUlf9JFO0BtgjzkWStVVXqmkVKl6aV0j4jA15wARvZTin79XMMjZLMD_0nDtAnExYeLz7nX19cprzyaVJ5UguBKvnkF_1llghH7szKPg2TYlAgTA11cIhp4Hu1u1NYHfyF0j7tfjcJkWr_033ZruSkwexbsJfvq_0WxeT_0qXm_0wevFYqa8fAiu1o_1L_0G61mG9ZqihnMk55FWv2mHI1prtRxZmXX2uuIAIlVZ98oP9BSGh5lG2AwQILFlqwyIKNLdjEgk0t2FcLdmHBLm2arYHYIglOQwEw5xWB_1QLhrKIMNlwUssYpgbcCy3ydU1JmMMMKA8YZ8UEE0hJLucIVAScsx0qcYWqtAI_1XRGDFhfSOSuGhh4cuuFF6hmSryFwQ0vEtaSKwaEF0SumY8X7hEXpWVEfbIrQh6onzsqBqRgVJ9dStc47AoesOvgyOPSGt6hK_04FcME8q0x4nGfyOK1wpmvGElxxnJ4Of_1RZdJVkkTm6Ja7NZfUSLntPyjhAKAkAEONzNCmhshTa4fu_11sAL2hjW987KoDeS_0rA55duX4seNMj3NHA7JzOhJ2JRgPzdawD79XKuoxTLNXVYTCjLi1bytR0PLjWorWH0_19hk_1HCk8XMcV3N6jiGYvrXKPTEpkO55zFogig8h0Cn8jbHjJGyC8S0t85cdOayz6nf2z65QXhaKbujgdZc7Hv1Bw4FQIEwJwvQHUPxmqbgvcQK0u7fKyIl3pD92P8Ns2gyBw

#include <SPL/Toolkit/JavaOp.h>


#ifndef SPL_OPER_INSTANCE_SINK_KAFKASINK_KAFKASINKOP_H_
#define SPL_OPER_INSTANCE_SINK_KAFKASINK_KAFKASINKOP_H_

#include <SPL/Runtime/Operator/Operator.h>
#include <SPL/Runtime/Operator/ParameterValue.h>
#include <SPL/Runtime/Operator/OperatorContext.h>
#include <SPL/Runtime/Operator/OperatorMetrics.h>
#include <SPL/Runtime/Operator/Port/AutoPortMutex.h>
#include <SPL/Runtime/Operator/State/StateHandler.h>
#include <SPL/Runtime/ProcessingElement/PE.h>
#include <SPL/Runtime/Type/SPLType.h>
#include <SPL/Runtime/Utility/CV.h>
using namespace UTILS_NAMESPACE;

#include "../../../type/BeJwrMS42LckvyEwuNs5OrSw2z00tLk5MTwUAaSIIDt.h"
#include <SPL_JNIFunctions_com_ibm_iot4i_common.h>


#define MY_OPERATOR KafkaSinkOp$OP
#define MY_BASE_OPERATOR KafkaSinkOp_Base
#define MY_OPERATOR_SCOPE SPL::_Operator::sink::kafkaSink

namespace SPL {
namespace _Operator {
namespace sink {
namespace kafkaSink {

class MY_BASE_OPERATOR : public Operator
{
public:
    
    typedef SPL::BeJwrMS42LckvyEwuNs5OrSw2z00tLk5MTwUAaSIIDt IPort0Type;
    
    MY_BASE_OPERATOR();
    
    ~MY_BASE_OPERATOR();
    
    inline void tupleLogic(Tuple const & tuple, uint32_t port);
    void processRaw(Tuple const & tuple, uint32_t port);
    
    inline void punctLogic(Punctuation const & punct, uint32_t port);
    void processRaw(Punctuation const & punct, uint32_t port);
    void punctPermitProcessRaw(Punctuation const & punct, uint32_t port);
    void punctNoPermitProcessRaw(Punctuation const & punct, uint32_t port);
    virtual void javaopInternalAction(Punctuation const & punct, uint32_t port) = 0;
    
    
    
    SPL::rstring lit$0;
    SPL::rstring lit$1;
    SPL::rstring lit$2;
    SPL::rstring lit$3;
    SPL::rstring lit$4;
    SPL::rstring lit$5;
    SPL::rstring lit$6;
    SPL::rstring lit$7;
    SPL::rstring lit$8;
    SPL::rstring lit$9;
    SPL::rstring lit$10;
    SPL::rstring lit$11;
    SPL::rstring lit$12;
    
    
protected:
    Mutex $svMutex;
    SPL::rstring param$className$0;
    SPL::rstring param$classLibrary$0;
    SPL::rstring param$classLibrary$1;
    SPL::rstring param$classLibrary$2;
    SPL::rstring param$propertiesFile$0;
    void checkpointStateVariables(NetworkByteBuffer & opstate) const;
    void restoreStateVariables(NetworkByteBuffer & opstate);
    void checkpointStateVariables(Checkpoint & ckpt);
    void resetStateVariables(Checkpoint & ckpt);
    void resetStateVariablesToInitialState();
    bool hasStateVariables() const;
    void resetToInitialStateRaw();
    void checkpointRaw(Checkpoint & ckpt);
    void resetRaw(Checkpoint & ckpt);
private:
    static bool globalInit_;
    static bool globalIniter();
    ParameterMapType paramValues_;
    ParameterMapType& getParameters() { return paramValues_;}
    void addParameterValue(std::string const & param, ConstValueHandle const& value)
    {
        ParameterMapType::iterator it = paramValues_.find(param);
        if (it == paramValues_.end())
            it = paramValues_.insert (std::make_pair (param, ParameterValueListType())).first;
        it->second.push_back(&ParameterValue::create(value));
    }
    void addParameterValue(std::string const & param)
    {
        ParameterMapType::iterator it = paramValues_.find(param);
        if (it == paramValues_.end())
            it = paramValues_.insert (std::make_pair (param, ParameterValueListType())).first;
        it->second.push_back(&ParameterValue::create());
    }
};


class MY_OPERATOR : public MY_BASE_OPERATOR 
{
public:
  // constructor
  MY_OPERATOR();

  // destructor
  virtual ~MY_OPERATOR(); 

  // notify port readiness
  void allPortsReady(); 

  // notify termination
  void prepareToShutdown(); 

  // processing for source and threaded operators   
  void process(uint32_t idx);
    
  // tuple processing for mutating ports 
  void process(Tuple & tuple, uint32_t port);
    
  // tuple processing for non-mutating ports
  void process(Tuple const & tuple, uint32_t port);

  // punctuation processing
  void process(Punctuation const & punct, uint32_t port);
private:
  void setupStateHandler();

  // members
  
  /** How we invoke actions on the Java Operator implementation */
  SPL::JNI::JNIBridgeInvoker* _bi;
  
  /* The instance of the JNIBridge used to wrap the Java Operator implementation */
  jobject _bridge;

  typedef void (*FP)(SPL::JNI::JNIBridgeInvoker&, jobject, NativeByteBuffer &, uint32_t);
  FP _fp;
  
  bool hasTupleLogic;
  
  std::tr1::shared_ptr<SPL::StateHandler> _stateHandler;

public:
  virtual void javaopInternalAction(Punctuation const & punct, uint32_t port)
  {
     _bi->action(_bridge, punct, port);
  }
    
  // handle byte buffers being send as is
  virtual void processRaw(NativeByteBuffer & buffer, uint32_t port)
  {
     (*_fp)(*_bi, _bridge, buffer, port);
  }
  
  //Java operators handle byte buffers directly
  virtual bool sendRawBufferData() { return !hasTupleLogic; }

  // Requests the blocking of a consistent region permit
  virtual void blockConsistentRegionPermit() {
      _bi->blockConsistentRegionPermit(_bridge);
  }
}; 

} // namespace sink
} // namespace kafkaSink
} // namespace _Operator
} // namespace SPL

#undef MY_OPERATOR_SCOPE
#undef MY_BASE_OPERATOR
#undef MY_OPERATOR
#endif // SPL_OPER_INSTANCE_SINK_KAFKASINK_KAFKASINKOP_H_


// eJzFk9FO2zAUhnXeBEW7aDcRJ23HWO46RsUgtBMNu3fcs_0YssR1styhvj0mBqUm5gBuURLKPvqPvt2MLLUPK_1afdhELconI2FFpKrZLkrEBRToWjLXek1aKGGCL_1xBCEIfOva2pkP_1Dy3lwvJ6OrJl6kfy_0vr7J8kd1Fdm6_1z9Pl10V2fjqbXpz_1mZbfwiLw_1ROoyH2KYPk7TRJjnSG1hrYWH6iNDtTGnRqwQktkfopcWr6SpNi9NqWtuUB2ZrgtlgVhtWIr7jgorTCCExAVt3bOJUJHvJ9tEIjXtyns7VIwhKnzjfnG4cwg7jQp5YabBkZd06CdzzZKPLbbJLl15NfYJMkaXaZ1VZL7SQaF06YZ7Odq8xzbdmnHbscGw_0HRl6N9jpGsK_1aPbznLSXmik_1DDIlWUs8_19PLym25u0_01fip3PTYzNdojpMxx16F_0zX6jA96tAOFVfuNXrcoR9vB9XavJztt1wNIDDt7QIYw8ai8dZnXYnNy1iitXyN8D9KBPrdUumlPV20J4x6ysg7HwBujl2X

#include <SPL/Toolkit/JavaOp.h>


#ifndef SPL_OPER_INSTANCE_CHECKACTIVATION_VALIDATEDSTREAM_H_
#define SPL_OPER_INSTANCE_CHECKACTIVATION_VALIDATEDSTREAM_H_

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

#include "../../type/BeJwrMS42Ky1OLfJMKTbOTq0sNs9NLS5OTE8FAHEVAk7.h"

#include <bitset>

#define MY_OPERATOR ValidatedStream$OP
#define MY_BASE_OPERATOR ValidatedStream_Base
#define MY_OPERATOR_SCOPE SPL::_Operator::checkActivation

namespace SPL {
namespace _Operator {
namespace checkActivation {

class MY_BASE_OPERATOR : public Operator
{
public:
    
    typedef SPL::BeJwrMS42Ky1OLfJMKTbOTq0sNs9NLS5OTE8FAHEVAk7 IPort0Type;
    typedef SPL::BeJwrMS42Ky1OLfJMKTbOTq0sNs9NLS5OTE8FAHEVAk7 OPort0Type;
    
    MY_BASE_OPERATOR();
    
    ~MY_BASE_OPERATOR();
    
    inline void tupleLogic(Tuple const & tuple, uint32_t port);
    void processRaw(Tuple const & tuple, uint32_t port);
    
    inline void punctLogic(Punctuation const & punct, uint32_t port);
    void processRaw(Punctuation const & punct, uint32_t port);
    void punctPermitProcessRaw(Punctuation const & punct, uint32_t port);
    void punctNoPermitProcessRaw(Punctuation const & punct, uint32_t port);
    virtual void javaopInternalAction(Punctuation const & punct, uint32_t port) = 0;
    
    inline void submit(Tuple & tuple, uint32_t port)
    {
        Operator::submit(tuple, port);
    }
    inline void submit(Punctuation const & punct, uint32_t port)
    {
        Operator::submit(punct, port);
    }
    
    
    
    SPL::rstring lit$0;
    SPL::rstring lit$1;
    SPL::rstring lit$2;
    SPL::rstring lit$3;
    
    
protected:
    Mutex $svMutex;
    typedef std::bitset<2> OPortBitsetType;
    OPortBitsetType $oportBitset;
    Mutex $fpMutex;
    SPL::rstring param$className$0;
    SPL::rstring param$classLibrary$0;
    SPL::rstring param$classLibrary$1;
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

} // namespace checkActivation
} // namespace _Operator
} // namespace SPL

#undef MY_OPERATOR_SCOPE
#undef MY_BASE_OPERATOR
#undef MY_OPERATOR
#endif // SPL_OPER_INSTANCE_CHECKACTIVATION_VALIDATEDSTREAM_H_


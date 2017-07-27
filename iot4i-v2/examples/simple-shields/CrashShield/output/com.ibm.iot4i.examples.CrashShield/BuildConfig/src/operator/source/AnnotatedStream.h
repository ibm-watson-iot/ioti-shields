// eJyNkllPAjEQxzOvfAo0PsDL7oJ3PRJUDCCX2dXEp02BkW326NoWFY3f3aGisoaoe2Y6R_1_1zm_0o8cRQm3AiZ8YSxy1k2NlJBDTy6t2HTcVx6zDxH9ww7T6rn79Sv5rVB977TuwpGg_0DB03192O_16u4OgeXDZaDVvG_1G_0E22uy5X1l3jPdC9Gw8D3ZkH8PDy7bsz19V2ySDj2h92w029bESRIh2OZhmJErzQ7IsRHzIwOey0nOiWF2nCDW_0FMo2pPgHIZU9ookU2LRg0SYbZqULd_1r_0hc_0opr4EYyRZdM5Knmk1Rk7pNUsc75GN1zxXXkRwKTiTvhhkMmM_1Rom3uRGFyws9VGUibIMzIrBaXljZOyFVKF5nOuUGtq1QIXuVRfAv8kBgKUndJrqUxXcY_0TMmO2zCpOxognYwSUPguijH0gZazXsvFhMXyK5sbWqyylObQSpiSZT7FSrR6V3kpAXGOcwye8pRe_06Xsg1zT2r2MEKfW4DcsRf5f8MTDvx1lY1bMmeLUZCq1U14j_0NemLADX3DnyEAfy



#ifndef SPL_OPER_INSTANCE_SOURCE_ANNOTATEDSTREAM_H_
#define SPL_OPER_INSTANCE_SOURCE_ANNOTATEDSTREAM_H_

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
#include "../../type/BeJwrMSo2zk6tLDbPTS0uTkxPBQAysQYl.h"
#include <SPL_JNIFunctions_com_ibm_iot4i_events_MH.h>

#include <bitset>

#define MY_OPERATOR AnnotatedStream$OP
#define MY_BASE_OPERATOR AnnotatedStream_Base
#define MY_OPERATOR_SCOPE SPL::_Operator::source

namespace SPL {
namespace _Operator {
namespace source {

class MY_BASE_OPERATOR : public Operator
{
public:
    
    typedef SPL::BeJwrMSo2zk6tLDbPTS0uTkxPBQAysQYl IPort0Type;
    typedef SPL::BeJwrMS42Ky1OLfJMKTbOTq0sNs9NLS5OTE8FAHEVAk7 OPort0Type;
    
    MY_BASE_OPERATOR();
    
    ~MY_BASE_OPERATOR();
    
    inline void tupleLogic(Tuple const & tuple, uint32_t port);
    void processRaw(Tuple const & tuple, uint32_t port);
    
    inline void punctLogic(Punctuation const & punct, uint32_t port);
    void processRaw(Punctuation const & punct, uint32_t port);
    void punctPermitProcessRaw(Punctuation const & punct, uint32_t port);
    void punctNoPermitProcessRaw(Punctuation const & punct, uint32_t port);
    
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
    
    SPL::rstring state$_userId;
    
protected:
    Mutex $svMutex;
    typedef std::bitset<2> OPortBitsetType;
    OPortBitsetType $oportBitset;
    Mutex $fpMutex;
    SPL::boolean param$filter$0;
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
   MY_OPERATOR() {}
  
   void process(Tuple const & tuple, uint32_t port);
   void process(Punctuation const & punct, uint32_t port);
   
       void getCheckpoint(NetworkByteBuffer & opstate) { checkpointStateVariables(opstate); }
       void restoreCheckpoint(NetworkByteBuffer & opstate) { restoreStateVariables(opstate); }
   
}; 

} // namespace source
} // namespace _Operator
} // namespace SPL

#undef MY_OPERATOR_SCOPE
#undef MY_BASE_OPERATOR
#undef MY_OPERATOR
#endif // SPL_OPER_INSTANCE_SOURCE_ANNOTATEDSTREAM_H_


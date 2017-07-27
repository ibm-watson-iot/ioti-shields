// eJyNUl1rwjAU5f6TInto2Whq1T2EMXDqwFG3SeazhJrZ0DYJSVxxv35JcCKiYyQPuV_1n3nNujGpSzRpquRS0wfh5J0orNfQhcyeHXpoid_01eMfTEXjq9IMO8KOuv_1azbvZrRmyZd_1p1ltqhHi49uNaZkPp_1atOpdKu1ysqoNWS7H02I2mQqflsEAGm5vMiDvBcbaWM3FFoKvf8GXn_1kAVbJlyJmMtoZuWi5QJ3VtFC0ZmmhqKlJx1mzQhloKQgrmqXmCXEl9bHxlROCggxbgSgLCb2ePIC8g_1EcfaB3qAKxUvDwBPOPbPxCu2f6vpDgOWkW3EcYhZFSzLqmxD6eJd1GwPhtJ7f0wesTYp8SHkrB39wcMxh_08ZRhvmfUPY2mr5oKw0sRJkrgeYVcJuJChW3Z9rqO8qYNae_0XixEn2A8nyL_1E



#ifndef SPL_OPER_INSTANCE_SINK_KAFKAPREPARE_H_
#define SPL_OPER_INSTANCE_SINK_KAFKAPREPARE_H_

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

#include "../../type/BeJwrMS42LckvyEwuNs5OrSw2z00tLk5MTwUAaSIIDt.h"
#include "../../type/BeJwrMSw2SUksSQQADLECDn.h"

#include <bitset>

#define MY_OPERATOR kafkaPrepare$OP
#define MY_BASE_OPERATOR kafkaPrepare_Base
#define MY_OPERATOR_SCOPE SPL::_Operator::sink

namespace SPL {
namespace _Operator {
namespace sink {

class MY_BASE_OPERATOR : public Operator
{
public:
    
    typedef SPL::BeJwrMSw2SUksSQQADLECDn IPort0Type;
    typedef SPL::BeJwrMS42LckvyEwuNs5OrSw2z00tLk5MTwUAaSIIDt OPort0Type;
    
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
    SPL::rstring lit$2;
    
    
protected:
    Mutex $svMutex;
    typedef std::bitset<2> OPortBitsetType;
    OPortBitsetType $oportBitset;
    Mutex $fpMutex;
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
   
}; 

} // namespace sink
} // namespace _Operator
} // namespace SPL

#undef MY_OPERATOR_SCOPE
#undef MY_BASE_OPERATOR
#undef MY_OPERATOR
#endif // SPL_OPER_INSTANCE_SINK_KAFKAPREPARE_H_


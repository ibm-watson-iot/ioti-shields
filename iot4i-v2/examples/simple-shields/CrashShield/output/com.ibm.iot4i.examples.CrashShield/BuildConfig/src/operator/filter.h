// eJylkF9rwjAUxclXkT3Yl6Y6x7YwBk6U_0V_0o7LVEe22DadIlEe233221ZSJsjBESksu5Ob97bC59A5I7oRWXjI2EdGBIhwS4uqTl_09QVOdA3mBzNPOx1p0VnOdtN5tP1Zrn_0DOzCPi9m4cNyPXwa9d_0HH_139o5_02yEu4mkWTxXh0UNvybxttdRaJDW7teiKCE89yCdZPXysrQlOdAbXOAM8sjzOh6FGbvc35FujAcJuGqQAZ05g7TpRWECDlrsZFO8Y2WkvgCr9jrCp8B2AMCRhDBDxKBsZqiEodXYtBOVMMtIpFWWiLXBt3F_1gJuCgDa3kCbc8jw1Nu8IWKKrKL6kzzl8iIIOacA7knBwtmHBODYQiVkD0Uzf1i3bxLU_10v0xu7Tp1mXWjGqoY_1q9veFdYvPSjFhlv2H5ualHHGLwV1Nf_1




#ifndef SPL_OPER_INSTANCE_FILTER_H_
#define SPL_OPER_INSTANCE_FILTER_H_

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

#include "../type/BeJwrMS42Ky1OLfJMKTbOTq0sNs9NLS5OTE8FAHEVAk7.h"
#include <SPL_JNIFunctions_com_ibm_iot4i_examples.h>

#include <bitset>

#define MY_OPERATOR filter$OP
#define MY_BASE_OPERATOR filter_Base
#define MY_OPERATOR_SCOPE SPL::_Operator

namespace SPL {
namespace _Operator {

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

} // namespace _Operator
} // namespace SPL

#undef MY_OPERATOR_SCOPE
#undef MY_BASE_OPERATOR
#undef MY_OPERATOR
#endif // SPL_OPER_INSTANCE_FILTER_H_


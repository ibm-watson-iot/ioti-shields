// eJyVUmtL60AQZf_0JiB_1aL3n4wOsiQpTItaYPSBH8FLbJmKx5bO7O1hh_1_1Z2sbRWlgoSEzObMnJNzBtvK0VAJI1UjKs5v101qlGY_08_0g6YYeO45q_0BfcaJp2exqfH970_1j54m0_1vlar785_0EML2ZRfDZfhn9ug7_1hQ1CeO8XhD419XB9HocKs96O3GNvo_0dGLy27dp2_0LGy_1HGDtznQfTIDibDpMu40WUTGZ3VhrJxCRVdSJXdCtzKhN4FXVbATrFFUn2WSXNkceoiXONRssmZ4y5harBpRJEjSKrZeN2SpfYihTcGy2wiAsJVeZmwgjWqAaGUU_0yMjCYYaetlKpANETCuT34LIpzUsU5yaLHoIvzrTCLTr6AC0hLyzuSrdKk2MnBJDUgihxG4zELX1tNFcFtGBvUu5LfhMEk0zZMRnGuEfRdxra_0lNDv3jfUu3ogVT_0S_1iZIVpOKb_1z_01trtwe4_1rRvv6NGYadGFL9AYpHDRXG7QVx_199vjzpINhF_1Z9Go0dkWX7jBc2o4XQtCn7lW72jFz6DxVyAzJ



#ifndef SPL_OPER_INSTANCE_SHIELD_H_
#define SPL_OPER_INSTANCE_SHIELD_H_

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
#include "../type/BeJwrMS42Ky1OLfJMySm2LEosdy1LzSspLjY0SkwuyczPC0gsSswtBgAMAA5M.h"
#include <SPL_JNIFunctions_com_ibm_iot4i_examples.h>

#include <bitset>

#define MY_OPERATOR shield$OP
#define MY_BASE_OPERATOR shield_Base
#define MY_OPERATOR_SCOPE SPL::_Operator

namespace SPL {
namespace _Operator {

class MY_BASE_OPERATOR : public Operator
{
public:
    
    typedef SPL::BeJwrMS42Ky1OLfJMKTbOTq0sNs9NLS5OTE8FAHEVAk7 IPort0Type;
    typedef SPL::BeJwrMS42Ky1OLfJMySm2LEosdy1LzSspLjY0SkwuyczPC0gsSswtBgAMAA5M OPort0Type;
    
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
#endif // SPL_OPER_INSTANCE_SHIELD_H_


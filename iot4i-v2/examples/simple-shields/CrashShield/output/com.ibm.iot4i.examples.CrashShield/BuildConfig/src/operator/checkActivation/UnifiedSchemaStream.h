// eJyNkF9PgzAUxXO_1yuLD9kI7_1M8b0y26scECMfHJdOMKDYViWzLx09stjsxI1LQvNznn3PO7uhaOQsEMlxUTnjdrqq2RCsZA7XNh4DjEftPWSCY436llfOEu2nEYvM6Xi2QTJm9Ur_1TtKogvw2R6M_1Mfpk9_0ce3kgz6vdD_0KKxPcb6Ikpk1SvEeTtd_1q9bPYG6hdK7g5oxBHgecpbRSvMgAguSyR2BFZqVla8orspCp0zbZI7hTTeZxzFClJmWFQyQrpIYwCr6XqAv8sARzUARwseoEtHCuUqDXLsJv30bIn_0l_03gdJuOYdGo3pMTyLH37GPtzjt8UPUEToZmhcrHY56yv5q_0pIPRxbqE4BZKF6



#ifndef SPL_OPER_INSTANCE_CHECKACTIVATION_UNIFIEDSCHEMASTREAM_H_
#define SPL_OPER_INSTANCE_CHECKACTIVATION_UNIFIEDSCHEMASTREAM_H_

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

#include <bitset>

#define MY_OPERATOR UnifiedSchemaStream$OP
#define MY_BASE_OPERATOR UnifiedSchemaStream_Base
#define MY_OPERATOR_SCOPE SPL::_Operator::checkActivation

namespace SPL {
namespace _Operator {
namespace checkActivation {

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

} // namespace checkActivation
} // namespace _Operator
} // namespace SPL

#undef MY_OPERATOR_SCOPE
#undef MY_BASE_OPERATOR
#undef MY_OPERATOR
#endif // SPL_OPER_INSTANCE_CHECKACTIVATION_UNIFIEDSCHEMASTREAM_H_


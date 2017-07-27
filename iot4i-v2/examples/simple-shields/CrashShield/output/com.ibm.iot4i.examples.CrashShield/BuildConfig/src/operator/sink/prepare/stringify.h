// eJylUl1v2jAUlV_17K7JqD4lUkQ9gqry2ElCmkQYKMjzsKXKJAZfEzmzTNFT973UcylqUPUxzvo_1vvTn3niPztCVIihXlDKcQ_1tixpeIC_0MDTRwect1pufaoyJ26fhIUYo05wV_1r30SoclygLoiGXSelHeyTz6PGXh7bFrlzupwNvLZEsVH_1dG_1d63XFrc_16XekWAFluJZrPebTQc3LLmyFJMRl57jrzfJb0s7xaXsnychvvhUzTx_1HCO8smk9HK0fb7vzwZdMtt1qzpXaBrF4WRkOtNdynjJs5g_06IurDo03eI9FYtCM6z_1f6L6_1gZSqrx7QqRAKqQRla2AwvwELGrB2A9ZpwLo1tqNMtQMAgLvhGXF1BMGZxElGmVtwsZU5XhJ3ILDcoA0laeImWGHAOCOe1mpFU0Uq0UyxB85Tgpn_0tGnOhW6ktSYq3kkiRontWF_0uLdOeA4bPuSBS6qkYwQ_1RdZX_1khpQIIyDXs4svejKspu5XNdcAsexTGS1Xo5v1YLQ0DnKB_0FCUZ1TQojzfC6W9jvdf_1cHhAUW7MJQaNePTv3oOt_0PLF7PXs8AaIOaNHjXT_0Bi_0ESYklpIqa4O8A3AhucUC63gMbgaL28Y76nzQQYqHY26f3JPXOeDw1A_02hpC7WAItbH1rXI2hAdrmx3tbZMUf87RYoSSM2QK_1zTxtvH5hdWk12f0OIHTjY8jsB1HN_18GLXBiA6



#ifndef SPL_OPER_INSTANCE_SINK_PREPARE_STRINGIFY_H_
#define SPL_OPER_INSTANCE_SINK_PREPARE_STRINGIFY_H_

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

#include "../../../type/BeJwrMS42Ky1OLfJMySm2LEosdy1LzSspLjY0SkwuyczPC0gsSswtBgAMAA5M.h"
#include "../../../type/BeJwrMSw2SUksSQQADLECDn.h"
#include "../../../type/BeJyrNI03TS0qyi8yKU8syjPJzEvLN01JTSpNNy0pSkxOBQC5eQu5.h"
#include <SPL_JNIFunctions_com_ibm_iot4i_hazards_common.h>

#include <bitset>

#define MY_OPERATOR stringify$OP
#define MY_BASE_OPERATOR stringify_Base
#define MY_OPERATOR_SCOPE SPL::_Operator::sink::prepare

namespace SPL {
namespace _Operator {
namespace sink {
namespace prepare {

class MY_BASE_OPERATOR : public Operator
{
public:
    
    typedef SPL::BeJwrMS42Ky1OLfJMySm2LEosdy1LzSspLjY0SkwuyczPC0gsSswtBgAMAA5M IPort0Type;
    typedef SPL::BeJwrMSw2SUksSQQADLECDn OPort0Type;
    
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
    SPL::rstring lit$3;
    SPL::rstring lit$4;
    SPL::uint32 lit$5;
    
    
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
} // namespace prepare
} // namespace _Operator
} // namespace SPL

#undef MY_OPERATOR_SCOPE
#undef MY_BASE_OPERATOR
#undef MY_OPERATOR
#endif // SPL_OPER_INSTANCE_SINK_PREPARE_STRINGIFY_H_


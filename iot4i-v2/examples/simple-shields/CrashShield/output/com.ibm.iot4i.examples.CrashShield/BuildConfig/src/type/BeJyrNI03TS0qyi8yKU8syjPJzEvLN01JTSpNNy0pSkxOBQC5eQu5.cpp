// y5_5error4warn4info5debug5trace


#include "BeJyrNI03TS0qyi8yKU8syjPJzEvLN01JTSpNNy0pSkxOBQC5eQu5.h"

#define SELF BeJyrNI03TS0qyi8yKU8syjPJzEvLN01JTSpNNy0pSkxOBQC5eQu5

using namespace SPL;

EnumMappings* SELF::mappings_ = SELF::initMappings();


SELF SELF::error(0);
SELF SELF::warn(1);
SELF SELF::info(2);
SELF SELF::debug(3);
SELF SELF::trace(4);

SELF::SELF(const std::string & v)
: Enum(*mappings_)
{
    // initialize from a string value
    this->Enum::operator=(v);
}

SELF::SELF(const rstring & v)
: Enum(*mappings_)
{
    // initialize from a string value
    this->Enum::operator=(v);
}


EnumMappings* SELF::initMappings()
{
   EnumMappings* em = new EnumMappings();
   // initialize the mappings 
   {
      std::string s("error");
      em->nameToIndex_.insert(std::make_pair(s, 0));
      em->indexToName_.push_back(s);
   }
   
   {
      std::string s("warn");
      em->nameToIndex_.insert(std::make_pair(s, 1));
      em->indexToName_.push_back(s);
   }
   
   {
      std::string s("info");
      em->nameToIndex_.insert(std::make_pair(s, 2));
      em->indexToName_.push_back(s);
   }
   
   {
      std::string s("debug");
      em->nameToIndex_.insert(std::make_pair(s, 3));
      em->indexToName_.push_back(s);
   }
   
   {
      std::string s("trace");
      em->nameToIndex_.insert(std::make_pair(s, 4));
      em->indexToName_.push_back(s);
   }
   
   return em;
}

// y5_5error4warn4info5debug5trace


#ifndef BEJYRNI03TS0QYI8YKU8SYJPJZEVLN01JTSPNNY0PSKXOBQC5EQU5_H_
#define BEJYRNI03TS0QYI8YKU8SYJPJZEVLN01JTSPNNY0PSKXOBQC5EQU5_H_

#include <SPL/Runtime/Type/Enum.h>

#define SELF BeJyrNI03TS0qyi8yKU8syjPJzEvLN01JTSpNNy0pSkxOBQC5eQu5

namespace SPL {

class SELF : public Enum
{
public:
   typedef SELF Self;

   static SELF error;
   static SELF warn;
   static SELF info;
   static SELF debug;
   static SELF trace;
   

   SELF() : Enum(*mappings_) { }
   SELF(uint32_t v) : Enum(*mappings_, v) { }
   SELF(const Self & ot) : Enum(ot) { }
   SELF(const Enum& ot) : Enum(*mappings_) { assignFrom(ot); }
   SELF(const ConstValueHandle & ot) : Enum(ot) { }
   SELF(const std::string & v);
   SELF(const rstring & v);

   virtual Enum * clone() const { return new Self(*this); }

   Self & operator=(const Self & ot) { index_ = ot.index_; return *this; }

   bool operator==(const Self & ot) const { return index_ == ot.index_; }
   bool operator!=(const Self & ot) const { return index_ != ot.index_; }
   bool operator>(const Self & ot) const { return index_ > ot.index_; }
   bool operator>=(const Self & ot) const { return index_ >= ot.index_; }
   bool operator<(const Self & ot) const { return index_ < ot.index_; }
   bool operator<=(const Self & ot) const { return index_ <= ot.index_; }

   bool operator==(const Enum & ot) const { return index_ == ot.getIndex(); }
   bool operator!=(const Enum & ot) const { return index_ != ot.getIndex(); }
   bool operator>(const Enum & ot) const { return index_ > ot.getIndex(); }
   bool operator>=(const Enum & ot) const { return index_ >= ot.getIndex(); }
   bool operator<(const Enum & ot) const { return index_ < ot.getIndex(); }
   bool operator<=(const Enum & ot) const { return index_ <= ot.getIndex(); }

   SELF& operator= (uint32_t v) { index_ = v; return *this; }

private:
   static EnumMappings* mappings_;
   static EnumMappings* initMappings();
};

// Helper for parsing CSV.
template <class T, unsigned char separator>
class CSVExtractor;

template <unsigned char separator>
class CSVExtractor<SELF, separator>
{
public:
    // Extract one token of type T from stream fs.
    static inline void extract(std::istream & fs, SELF& field) {
        field.deserialize(fs, separator);
    }
};

}  /* namespace SPL */

namespace std { 
    namespace tr1 {
        template<>
        struct hash<SPL::SELF> {
            inline size_t operator()(const SPL::SELF & self) const { return self.hashCode(); }
        };
    }
}

#undef SELF
#endif // BEJYRNI03TS0QYI8YKU8SYJPJZEVLN01JTSPNNY0PSKXOBQC5EQU5_H_

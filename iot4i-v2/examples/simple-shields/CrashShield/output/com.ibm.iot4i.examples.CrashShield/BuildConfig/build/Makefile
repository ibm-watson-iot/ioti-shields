# NOTE: This makefile is designed to be run from one level up in the directory
# hierarchy compared to where it resides. For instance, if this makefile is
# located at 'output/build/Makefile', then it can be launched as:
# 'make -f build/Makefile' from the 'output' directory.

SHELL=/bin/bash
ifeq ($(CXX),)
  CXX=g++
endif
PKGCONFIG := /opt/ibm/InfoSphere_Streams/4.2.1.1/bin/dst-pe-pkg-config.sh
SPL_BUNDLE_BUILDER := /opt/ibm/InfoSphere_Streams/4.2.1.1/system/impl/bin/spl-bundle-builder
SO_INCLUDE := $(shell $(PKGCONFIG) --cflags dst-spl-pe-install)
SO_LIB := $(shell $(PKGCONFIG) --libs dst-spl-pe-install)
SO_STAND_LIB := $(shell $(PKGCONFIG) --libs dst-spl-pe-install-standalone)
SPL_CXXFLAGS = -Wall -Wno-unused -Wno-sign-compare
SPL_CXXFLAGS += -D_REENTRANT -fPIC -finline-functions -fmessage-length=0
SPL_CXXFLAGS += $(SO_INCLUDE) -O3 -DNDEBUG  -std=gnu++98 
SPL_LDFLAGS = $(SO_LIB)

SPL_TYPES = build/type/BeJwrMSo2zk6tLDbPTS0uTkxPBQAysQYl.o \
            build/type/BeJwrMS42Ky1OLfJMKTbOTq0sNs9NLS5OTE8FAHEVAk7.o \
            build/type/BeJyrNI03TS0qyi8yKU8syjPJzEvLN01JTSpNNy0pSkxOBQC5eQu5.o \
            build/type/BeJwrMSw2SUksSQQADLECDn.o \
            build/type/BeJwrMS42Ky1OLfJMySm2LEosdy1LzSspLjY0SkwuyczPC0gsSswtBgAMAA5M.o \
            build/type/BeJwrMS42LckvyEwuNs5OrSw2z00tLk5MTwUAaSIIDt.o 
SPL_FUNCTIONS = 
SPL_OPERATORS = build/operator/filter.o \
                build/operator/shield.o \
                build/operator/source/AnnotatedStream.o \
                build/operator/source/source/SourceStream.o \
                build/operator/checkActivation/UnifiedSchemaStream.o \
                build/operator/checkActivation/ValidatedStream.o \
                build/operator/checkActivation/source/SourceStream.o \
                build/operator/sink/kafkaPrepare.o \
                build/operator/sink/prepare/stringify.o \
                build/operator/sink/kafkaSink/KafkaSinkOp.o 
SPL_SO_BINS = bin/filter.so \
              bin/shield.so \
              bin/source.AnnotatedStream.so \
              bin/source.source.SourceStream.so \
              bin/checkActivation.UnifiedSchemaStream.so \
              bin/checkActivation.ValidatedStream.so \
              bin/checkActivation.source.SourceStream.so \
              bin/sink.kafkaPrepare.so \
              bin/sink.prepare.stringify.so \
              bin/sink.kafkaSink.KafkaSinkOp.so 

SPL_ADL_FILE = com.ibm.iot4i.examples.CrashShield.adl

SPL_OUTPUT_DIR_ROOT = $(shell printf '%q' '/home/streamsadmin/workspace/CrashShield/output/com.ibm.iot4i.examples.CrashShield')
SPL_OUTPUT_DIR = $(shell printf '%q' 'BuildConfig')

.PHONY: distclean clean all types functions operators sos bundle toolkits standalone sablink

all: bin types functions operators sos standalone bundle sablink

distclean clean:
	@rm -fr bin/* build/type build/function build/operator build/bundle build/standalone com.ibm.iot4i.examples.CrashShield.sab

bin:
	@mkdir -m 775 bin

types: $(SPL_TYPES)

build/type/BeJyrNI03TS0qyi8yKU8syjPJzEvLN01JTSpNNy0pSkxOBQC5eQu5.o: \
	    src/type/BeJyrNI03TS0qyi8yKU8syjPJzEvLN01JTSpNNy0pSkxOBQC5eQu5.h \
	    src/type/BeJyrNI03TS0qyi8yKU8syjPJzEvLN01JTSpNNy0pSkxOBQC5eQu5.cpp \
	    build/cppOptions \
	    | build/type
	@echo ' [CXX-type] enum{error,warn,info,debug,trace}'
	@$(CXX) -o $@ -c $(SPL_CXXFLAGS) src/type/BeJyrNI03TS0qyi8yKU8syjPJzEvLN01JTSpNNy0pSkxOBQC5eQu5.cpp

build/type/BeJwrMSo2zk6tLDbPTS0uTkxPBQAysQYl.o: \
	    src/type/BeJwrMSo2zk6tLDbPTS0uTkxPBQAysQYl.h \
	    src/type/BeJwrMSo2zk6tLDbPTS0uTkxPBQAysQYl.cpp \
	    build/cppOptions \
	    | build/type
	@echo ' [CXX-type] tuple<rstring key,rstring message>'
	@$(CXX) -o $@ -c $(SPL_CXXFLAGS) src/type/BeJwrMSo2zk6tLDbPTS0uTkxPBQAysQYl.cpp

build/type/BeJwrMS42Ky1OLfJMKTbOTq0sNs9NLS5OTE8FAHEVAk7.o: \
	    src/type/BeJwrMS42Ky1OLfJMKTbOTq0sNs9NLS5OTE8FAHEVAk7.h \
	    src/type/BeJwrMS42Ky1OLfJMKTbOTq0sNs9NLS5OTE8FAHEVAk7.cpp \
	    build/cppOptions \
	    | build/type
	@echo ' [CXX-type] tuple<rstring userId,rstring key,rstring message>'
	@$(CXX) -o $@ -c $(SPL_CXXFLAGS) src/type/BeJwrMS42Ky1OLfJMKTbOTq0sNs9NLS5OTE8FAHEVAk7.cpp

build/type/BeJwrMSw2SUksSQQADLECDn.o: \
	    src/type/BeJwrMSw2SUksSQQADLECDn.h \
	    src/type/BeJwrMSw2SUksSQQADLECDn.cpp \
	    build/cppOptions \
	    | build/type
	@echo ' [CXX-type] tuple<rstring data>'
	@$(CXX) -o $@ -c $(SPL_CXXFLAGS) src/type/BeJwrMSw2SUksSQQADLECDn.cpp

build/type/BeJwrMS42Ky1OLfJMySm2LEosdy1LzSspLjY0SkwuyczPC0gsSswtBgAMAA5M.o: \
	    src/type/BeJwrMS42Ky1OLfJMySm2LEosdy1LzSspLjY0SkwuyczPC0gsSswtBgAMAA5M.h \
	    src/type/BeJwrMS42Ky1OLfJMySm2LEosdy1LzSspLjY0SkwuyczPC0gsSswtBgAMAA5M.cpp \
	    build/cppOptions \
	    | build/type
	@echo ' [CXX-type] tuple<rstring userId,list<rstring> rawEvents,rstring actionParams>'
	@$(CXX) -o $@ -c $(SPL_CXXFLAGS) src/type/BeJwrMS42Ky1OLfJMySm2LEosdy1LzSspLjY0SkwuyczPC0gsSswtBgAMAA5M.cpp

build/type/BeJwrMS42LckvyEwuNs5OrSw2z00tLk5MTwUAaSIIDt.o: \
	    src/type/BeJwrMS42LckvyEwuNs5OrSw2z00tLk5MTwUAaSIIDt.h \
	    src/type/BeJwrMS42LckvyEwuNs5OrSw2z00tLk5MTwUAaSIIDt.cpp \
	    build/cppOptions \
	    | build/type
	@echo ' [CXX-type] tuple<rstring topic,rstring key,rstring message>'
	@$(CXX) -o $@ -c $(SPL_CXXFLAGS) src/type/BeJwrMS42LckvyEwuNs5OrSw2z00tLk5MTwUAaSIIDt.cpp

build/type:
	-@mkdir -m 775 build/type

operators: $(SPL_OPERATORS)

-include build/operator/filter.d
build/operator/filter.o: \
	    src/operator/filter.h \
	    src/operator/filter.cpp\
	    build/dep/operator/filter.dep  \
	    build/cppOptions \
	    | build/operator
	@echo ' [CXX-operator] filter'
	@$(CXX) -o $@ -MD -I /opt/ibm/InfoSphere_Streams/4.2.1.1/toolkits/spl/impl/nl/include -I ../../.././com.ibm.iot4i.examples/native.function/. -I /opt/ibm/InfoSphere_Streams/4.2.1.1/java/include -DSPLJAVAFUNCTION_ADL_FILENAME=\"com.ibm.iot4i.examples.CrashShield.adl\" -c $(SPL_CXXFLAGS) src/operator/filter.cpp

-include build/operator/shield.d
build/operator/shield.o: \
	    src/operator/shield.h \
	    src/operator/shield.cpp\
	    build/dep/operator/shield.dep  \
	    build/cppOptions \
	    | build/operator
	@echo ' [CXX-operator] shield'
	@$(CXX) -o $@ -MD -I /opt/ibm/InfoSphere_Streams/4.2.1.1/toolkits/spl/impl/nl/include -I ../../.././com.ibm.iot4i.examples/native.function/. -I /opt/ibm/InfoSphere_Streams/4.2.1.1/java/include -DSPLJAVAFUNCTION_ADL_FILENAME=\"com.ibm.iot4i.examples.CrashShield.adl\" -c $(SPL_CXXFLAGS) src/operator/shield.cpp

-include build/operator/source/AnnotatedStream.d
build/operator/source/AnnotatedStream.o: \
	    src/operator/source/AnnotatedStream.h \
	    src/operator/source/AnnotatedStream.cpp\
	    build/dep/operator/source/AnnotatedStream.dep  \
	    build/cppOptions \
	    | build/operator/source
	@echo ' [CXX-operator] source.AnnotatedStream'
	@$(CXX) -o $@ -MD -I /opt/ibm/InfoSphere_Streams/4.2.1.1/toolkits/spl/impl/nl/include -I /home/streamsadmin/workspace/iot4i-shield-toolkit/com.ibm.iot4i.events.MH/native.function/. -I /opt/ibm/InfoSphere_Streams/4.2.1.1/java/include -DSPLJAVAFUNCTION_ADL_FILENAME=\"com.ibm.iot4i.examples.CrashShield.adl\" -c $(SPL_CXXFLAGS) src/operator/source/AnnotatedStream.cpp

-include build/operator/source/source/SourceStream.d
build/operator/source/source/SourceStream.o: \
	    src/operator/source/source/SourceStream.h \
	    src/operator/source/source/SourceStream.cpp\
	    build/dep/operator/source/source/SourceStream.dep  \
	    build/cppOptions \
	    | build/operator/source/source
	@echo ' [CXX-operator] source.source.SourceStream'
	@$(CXX) -o $@ -MD -I /home/streamsadmin/Downloads/streamsx.kafka-1.0.0/build/release/com.ibm.streamsx.kafka/impl/nl/include -I /home/streamsadmin/workspace/iot4i-shield-toolkit/com.ibm.iot4i.common/native.function/. -I /opt/ibm/InfoSphere_Streams/4.2.1.1/java/include -I /opt/ibm/InfoSphere_Streams/4.2.1.1/toolkits/spl/spl/utility/JavaOp/../../../impl/include -DSPLJAVAFUNCTION_ADL_FILENAME=\"com.ibm.iot4i.examples.CrashShield.adl\" -c $(SPL_CXXFLAGS) src/operator/source/source/SourceStream.cpp

-include build/operator/checkActivation/UnifiedSchemaStream.d
build/operator/checkActivation/UnifiedSchemaStream.o: \
	    src/operator/checkActivation/UnifiedSchemaStream.h \
	    src/operator/checkActivation/UnifiedSchemaStream.cpp\
	    build/dep/operator/checkActivation/UnifiedSchemaStream.dep  \
	    build/cppOptions \
	    | build/operator/checkActivation
	@echo ' [CXX-operator] checkActivation.UnifiedSchemaStream'
	@$(CXX) -o $@ -MD -I /opt/ibm/InfoSphere_Streams/4.2.1.1/toolkits/spl/impl/nl/include -DSPLJAVAFUNCTION_ADL_FILENAME=\"com.ibm.iot4i.examples.CrashShield.adl\" -c $(SPL_CXXFLAGS) src/operator/checkActivation/UnifiedSchemaStream.cpp

-include build/operator/checkActivation/ValidatedStream.d
build/operator/checkActivation/ValidatedStream.o: \
	    src/operator/checkActivation/ValidatedStream.h \
	    src/operator/checkActivation/ValidatedStream.cpp\
	    build/dep/operator/checkActivation/ValidatedStream.dep  \
	    build/cppOptions \
	    | build/operator/checkActivation
	@echo ' [CXX-operator] checkActivation.ValidatedStream'
	@$(CXX) -o $@ -MD -I /home/streamsadmin/workspace/iot4i-shield-toolkit/impl/nl/include -I /opt/ibm/InfoSphere_Streams/4.2.1.1/toolkits/spl/spl/utility/JavaOp/../../../impl/include -I /opt/ibm/InfoSphere_Streams/4.2.1.1/java/include -DSPLJAVAFUNCTION_ADL_FILENAME=\"com.ibm.iot4i.examples.CrashShield.adl\" -c $(SPL_CXXFLAGS) src/operator/checkActivation/ValidatedStream.cpp

-include build/operator/checkActivation/source/SourceStream.d
build/operator/checkActivation/source/SourceStream.o: \
	    src/operator/checkActivation/source/SourceStream.h \
	    src/operator/checkActivation/source/SourceStream.cpp\
	    build/dep/operator/checkActivation/source/SourceStream.dep  \
	    build/cppOptions \
	    | build/operator/checkActivation/source
	@echo ' [CXX-operator] checkActivation.source.SourceStream'
	@$(CXX) -o $@ -MD -I /home/streamsadmin/Downloads/streamsx.kafka-1.0.0/build/release/com.ibm.streamsx.kafka/impl/nl/include -I /home/streamsadmin/workspace/iot4i-shield-toolkit/com.ibm.iot4i.common/native.function/. -I /opt/ibm/InfoSphere_Streams/4.2.1.1/java/include -I /opt/ibm/InfoSphere_Streams/4.2.1.1/toolkits/spl/spl/utility/JavaOp/../../../impl/include -DSPLJAVAFUNCTION_ADL_FILENAME=\"com.ibm.iot4i.examples.CrashShield.adl\" -c $(SPL_CXXFLAGS) src/operator/checkActivation/source/SourceStream.cpp

-include build/operator/sink/kafkaPrepare.d
build/operator/sink/kafkaPrepare.o: \
	    src/operator/sink/kafkaPrepare.h \
	    src/operator/sink/kafkaPrepare.cpp\
	    build/dep/operator/sink/kafkaPrepare.dep  \
	    build/cppOptions \
	    | build/operator/sink
	@echo ' [CXX-operator] sink.kafkaPrepare'
	@$(CXX) -o $@ -MD -I /opt/ibm/InfoSphere_Streams/4.2.1.1/toolkits/spl/impl/nl/include -DSPLJAVAFUNCTION_ADL_FILENAME=\"com.ibm.iot4i.examples.CrashShield.adl\" -c $(SPL_CXXFLAGS) src/operator/sink/kafkaPrepare.cpp

-include build/operator/sink/prepare/stringify.d
build/operator/sink/prepare/stringify.o: \
	    src/operator/sink/prepare/stringify.h \
	    src/operator/sink/prepare/stringify.cpp\
	    build/dep/operator/sink/prepare/stringify.dep \
	    src/type/BeJyrNI03TS0qyi8yKU8syjPJzEvLN01JTSpNNy0pSkxOBQC5eQu5.h \
	    src/type/BeJwrMSw2SUksSQQADLECDn.h  \
	    build/cppOptions \
	    | build/operator/sink/prepare
	@echo ' [CXX-operator] sink.prepare.stringify'
	@$(CXX) -o $@ -MD -I /opt/ibm/InfoSphere_Streams/4.2.1.1/toolkits/spl/impl/nl/include -I /home/streamsadmin/workspace/iot4i-shield-toolkit/com.ibm.iot4i.hazards.common/native.function/. -I /opt/ibm/InfoSphere_Streams/4.2.1.1/java/include -DSPLJAVAFUNCTION_ADL_FILENAME=\"com.ibm.iot4i.examples.CrashShield.adl\" -c $(SPL_CXXFLAGS) src/operator/sink/prepare/stringify.cpp

-include build/operator/sink/kafkaSink/KafkaSinkOp.d
build/operator/sink/kafkaSink/KafkaSinkOp.o: \
	    src/operator/sink/kafkaSink/KafkaSinkOp.h \
	    src/operator/sink/kafkaSink/KafkaSinkOp.cpp\
	    build/dep/operator/sink/kafkaSink/KafkaSinkOp.dep  \
	    build/cppOptions \
	    | build/operator/sink/kafkaSink
	@echo ' [CXX-operator] sink.kafkaSink.KafkaSinkOp'
	@$(CXX) -o $@ -MD -I /home/streamsadmin/Downloads/streamsx.kafka-1.0.0/build/release/com.ibm.streamsx.kafka/impl/nl/include -I /home/streamsadmin/workspace/iot4i-shield-toolkit/com.ibm.iot4i.common/native.function/. -I /opt/ibm/InfoSphere_Streams/4.2.1.1/java/include -I /opt/ibm/InfoSphere_Streams/4.2.1.1/toolkits/spl/spl/utility/JavaOp/../../../impl/include -DSPLJAVAFUNCTION_ADL_FILENAME=\"com.ibm.iot4i.examples.CrashShield.adl\" -c $(SPL_CXXFLAGS) src/operator/sink/kafkaSink/KafkaSinkOp.cpp

build/operator/sink/kafkaSink:
	-@mkdir -m 775 -p build/operator/sink/kafkaSink

build/operator:
	-@mkdir -m 775 -p build/operator

build/operator/sink:
	-@mkdir -m 775 -p build/operator/sink

build/operator/source/source:
	-@mkdir -m 775 -p build/operator/source/source

build/operator/sink/prepare:
	-@mkdir -m 775 -p build/operator/sink/prepare

build/operator/source:
	-@mkdir -m 775 -p build/operator/source

build/operator/checkActivation/source:
	-@mkdir -m 775 -p build/operator/checkActivation/source

build/operator/checkActivation:
	-@mkdir -m 775 -p build/operator/checkActivation

functions: $(SPL_FUNCTIONS)

sos: $(SPL_SO_BINS)

SOfilter = \
	    build/operator/filter.o \
	    build/type/BeJwrMS42Ky1OLfJMKTbOTq0sNs9NLS5OTE8FAHEVAk7.o 

bin/filter.so: \
	$(SOfilter) \
	build/dep/filter.so \
	build/ldOptions
	@echo ' [LD-so] so filter'
	@$(CXX) $(SPL_CXXFLAGS) -o bin/filter.so -fPIC -shared $(SOfilter) -lstreams-stdtk-javaop -lstreams-stdtk-runtime -L /opt/ibm/InfoSphere_Streams/4.2.1.1/java/jre/lib/amd64/j9vm -Wl,-rpath -Wl,/opt/ibm/InfoSphere_Streams/4.2.1.1/java/jre/lib/amd64/j9vm -lpthread -ldl $(SPL_LDFLAGS)

SOshield = \
	    build/operator/shield.o \
	    build/type/BeJwrMS42Ky1OLfJMKTbOTq0sNs9NLS5OTE8FAHEVAk7.o \
	    build/type/BeJwrMS42Ky1OLfJMySm2LEosdy1LzSspLjY0SkwuyczPC0gsSswtBgAMAA5M.o 

bin/shield.so: \
	$(SOshield) \
	build/dep/shield.so \
	build/ldOptions
	@echo ' [LD-so] so shield'
	@$(CXX) $(SPL_CXXFLAGS) -o bin/shield.so -fPIC -shared $(SOshield) -lstreams-stdtk-javaop -lstreams-stdtk-runtime -L /opt/ibm/InfoSphere_Streams/4.2.1.1/java/jre/lib/amd64/j9vm -Wl,-rpath -Wl,/opt/ibm/InfoSphere_Streams/4.2.1.1/java/jre/lib/amd64/j9vm -lpthread -ldl $(SPL_LDFLAGS)

SOsource.AnnotatedStream = \
	    build/operator/source/AnnotatedStream.o \
	    build/type/BeJwrMS42Ky1OLfJMKTbOTq0sNs9NLS5OTE8FAHEVAk7.o \
	    build/type/BeJwrMSo2zk6tLDbPTS0uTkxPBQAysQYl.o 

bin/source.AnnotatedStream.so: \
	$(SOsource.AnnotatedStream) \
	build/dep/source.AnnotatedStream.so \
	build/ldOptions
	@echo ' [LD-so] so source.AnnotatedStream'
	@$(CXX) $(SPL_CXXFLAGS) -o bin/source.AnnotatedStream.so -fPIC -shared $(SOsource.AnnotatedStream) -lstreams-stdtk-javaop -lstreams-stdtk-runtime -L /opt/ibm/InfoSphere_Streams/4.2.1.1/java/jre/lib/amd64/j9vm -Wl,-rpath -Wl,/opt/ibm/InfoSphere_Streams/4.2.1.1/java/jre/lib/amd64/j9vm -lpthread -ldl $(SPL_LDFLAGS)

SOsource.source.SourceStream = \
	    build/operator/source/source/SourceStream.o \
	    build/type/BeJwrMSo2zk6tLDbPTS0uTkxPBQAysQYl.o 

bin/source.source.SourceStream.so: \
	$(SOsource.source.SourceStream) \
	build/dep/source.source.SourceStream.so \
	build/ldOptions
	@echo ' [LD-so] so source.source.SourceStream'
	@$(CXX) $(SPL_CXXFLAGS) -o bin/source.source.SourceStream.so -fPIC -shared $(SOsource.source.SourceStream) -lstreams-stdtk-javaop -lstreams-stdtk-runtime -L /opt/ibm/InfoSphere_Streams/4.2.1.1/java/jre/lib/amd64/j9vm -Wl,-rpath -Wl,/opt/ibm/InfoSphere_Streams/4.2.1.1/java/jre/lib/amd64/j9vm -lpthread -ldl -lstreams-stdtk-javaop -lpthread -ldl -lstreams-stdtk-runtime $(SPL_LDFLAGS)

SOcheckActivation.UnifiedSchemaStream = \
	    build/operator/checkActivation/UnifiedSchemaStream.o \
	    build/type/BeJwrMS42Ky1OLfJMKTbOTq0sNs9NLS5OTE8FAHEVAk7.o \
	    build/type/BeJwrMSo2zk6tLDbPTS0uTkxPBQAysQYl.o 

bin/checkActivation.UnifiedSchemaStream.so: \
	$(SOcheckActivation.UnifiedSchemaStream) \
	build/dep/checkActivation.UnifiedSchemaStream.so \
	build/ldOptions
	@echo ' [LD-so] so checkActivation.UnifiedSchemaStream'
	@$(CXX) $(SPL_CXXFLAGS) -o bin/checkActivation.UnifiedSchemaStream.so -fPIC -shared $(SOcheckActivation.UnifiedSchemaStream) $(SPL_LDFLAGS)

SOcheckActivation.ValidatedStream = \
	    build/operator/checkActivation/ValidatedStream.o \
	    build/type/BeJwrMS42Ky1OLfJMKTbOTq0sNs9NLS5OTE8FAHEVAk7.o 

bin/checkActivation.ValidatedStream.so: \
	$(SOcheckActivation.ValidatedStream) \
	build/dep/checkActivation.ValidatedStream.so \
	build/ldOptions
	@echo ' [LD-so] so checkActivation.ValidatedStream'
	@$(CXX) $(SPL_CXXFLAGS) -o bin/checkActivation.ValidatedStream.so -fPIC -shared $(SOcheckActivation.ValidatedStream) -lstreams-stdtk-javaop -L /opt/ibm/InfoSphere_Streams/4.2.1.1/java/jre/lib/amd64/j9vm -Wl,-rpath -Wl,/opt/ibm/InfoSphere_Streams/4.2.1.1/java/jre/lib/amd64/j9vm -lpthread -ldl -lstreams-stdtk-runtime $(SPL_LDFLAGS)

SOcheckActivation.source.SourceStream = \
	    build/operator/checkActivation/source/SourceStream.o \
	    build/type/BeJwrMSo2zk6tLDbPTS0uTkxPBQAysQYl.o 

bin/checkActivation.source.SourceStream.so: \
	$(SOcheckActivation.source.SourceStream) \
	build/dep/checkActivation.source.SourceStream.so \
	build/ldOptions
	@echo ' [LD-so] so checkActivation.source.SourceStream'
	@$(CXX) $(SPL_CXXFLAGS) -o bin/checkActivation.source.SourceStream.so -fPIC -shared $(SOcheckActivation.source.SourceStream) -lstreams-stdtk-javaop -lstreams-stdtk-runtime -L /opt/ibm/InfoSphere_Streams/4.2.1.1/java/jre/lib/amd64/j9vm -Wl,-rpath -Wl,/opt/ibm/InfoSphere_Streams/4.2.1.1/java/jre/lib/amd64/j9vm -lpthread -ldl -lstreams-stdtk-javaop -lpthread -ldl -lstreams-stdtk-runtime $(SPL_LDFLAGS)

SOsink.kafkaPrepare = \
	    build/operator/sink/kafkaPrepare.o \
	    build/type/BeJwrMS42LckvyEwuNs5OrSw2z00tLk5MTwUAaSIIDt.o \
	    build/type/BeJwrMSw2SUksSQQADLECDn.o 

bin/sink.kafkaPrepare.so: \
	$(SOsink.kafkaPrepare) \
	build/dep/sink.kafkaPrepare.so \
	build/ldOptions
	@echo ' [LD-so] so sink.kafkaPrepare'
	@$(CXX) $(SPL_CXXFLAGS) -o bin/sink.kafkaPrepare.so -fPIC -shared $(SOsink.kafkaPrepare) $(SPL_LDFLAGS)

SOsink.prepare.stringify = \
	    build/operator/sink/prepare/stringify.o \
	    build/type/BeJyrNI03TS0qyi8yKU8syjPJzEvLN01JTSpNNy0pSkxOBQC5eQu5.o \
	    build/type/BeJwrMS42Ky1OLfJMySm2LEosdy1LzSspLjY0SkwuyczPC0gsSswtBgAMAA5M.o \
	    build/type/BeJwrMSw2SUksSQQADLECDn.o 

bin/sink.prepare.stringify.so: \
	$(SOsink.prepare.stringify) \
	build/dep/sink.prepare.stringify.so \
	build/ldOptions
	@echo ' [LD-so] so sink.prepare.stringify'
	@$(CXX) $(SPL_CXXFLAGS) -o bin/sink.prepare.stringify.so -fPIC -shared $(SOsink.prepare.stringify) -lstreams-stdtk-javaop -lstreams-stdtk-runtime -L /opt/ibm/InfoSphere_Streams/4.2.1.1/java/jre/lib/amd64/j9vm -Wl,-rpath -Wl,/opt/ibm/InfoSphere_Streams/4.2.1.1/java/jre/lib/amd64/j9vm -lpthread -ldl $(SPL_LDFLAGS)

SOsink.kafkaSink.KafkaSinkOp = \
	    build/operator/sink/kafkaSink/KafkaSinkOp.o \
	    build/type/BeJwrMS42LckvyEwuNs5OrSw2z00tLk5MTwUAaSIIDt.o 

bin/sink.kafkaSink.KafkaSinkOp.so: \
	$(SOsink.kafkaSink.KafkaSinkOp) \
	build/dep/sink.kafkaSink.KafkaSinkOp.so \
	build/ldOptions
	@echo ' [LD-so] so sink.kafkaSink.KafkaSinkOp'
	@$(CXX) $(SPL_CXXFLAGS) -o bin/sink.kafkaSink.KafkaSinkOp.so -fPIC -shared $(SOsink.kafkaSink.KafkaSinkOp) -lstreams-stdtk-javaop -lstreams-stdtk-runtime -L /opt/ibm/InfoSphere_Streams/4.2.1.1/java/jre/lib/amd64/j9vm -Wl,-rpath -Wl,/opt/ibm/InfoSphere_Streams/4.2.1.1/java/jre/lib/amd64/j9vm -lpthread -ldl -lstreams-stdtk-javaop -lpthread -ldl -lstreams-stdtk-runtime $(SPL_LDFLAGS)

standalone: bin/standalone.exe

build/standalone/standalone.o: \
	    src/standalone/standalone.h \
	    src/standalone/standalone.cpp  \
	    build/cppOptions \
	    | build/standalone
	@echo ' [CXX-standalone] standalone'
	@$(CXX) -o $@ -c $(SPL_CXXFLAGS) src/standalone/standalone.cpp

bin/standalone.exe: build/standalone/standalone.o build/ldOptions
	@echo ' [LD-standalone] standalone'
	@$(CXX) $(SPL_CXXFLAGS) -o bin/standalone.exe build/standalone/standalone.o $(SO_STAND_LIB)
	@echo ' [LN-standalone] standalone '
	@ln -sf standalone bin/com.ibm.iot4i.examples.CrashShield
build/standalone:
	-@mkdir -m 775 build/standalone

sablink:

	@ln -sf ../com.ibm.iot4i.examples.CrashShield.sab bin/sab

bundle: com.ibm.iot4i.examples.CrashShield.sab

-include build/dep/com.ibm.iot4i.examples.CrashShield.sab.d
com.ibm.iot4i.examples.CrashShield.sab: $(SPL_SO_BINS) $(SPL_ADL_FILE) build/dep/com.ibm.iot4i.examples.CrashShield.sab.d bin/standalone.exe
	@echo ' [Bundle] com.ibm.iot4i.examples.CrashShield.sab'
	@$(SPL_BUNDLE_BUILDER) $(SPL_OUTPUT_DIR_ROOT) $(SPL_OUTPUT_DIR) $(SPL_ADL_FILE) com.ibm.iot4i.examples.CrashShield.sab


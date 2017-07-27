// eJxjBAAAAgAC

#include <SPL/Runtime/ProcessingElement/StandaloneApplication.h>

#include "standalone.h"

namespace Distillery 
{
    class DistilleryApplication;
}

Distillery::DistilleryApplication * instantiate_new_app() 
{ 
    SPL::StandaloneApplication app;
    return app.releaseImpl(); 
}

int main(int argc, char** argv, char** envp) 
{ 
    return SPL::StandaloneApplication::main(argc, argv, envp); 
}

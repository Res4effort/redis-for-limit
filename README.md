### redis-for-limit
 one of usage about redis
 if you want to limit someone's access to a method or class for a certain period of time, you can just use like this:

@AccessLimit(SpEL = "#yourMethod(#parameter)", times = xx, duration = xx, tipMsg = xx)
@RequestMapping("/hello")
@RequestBody
public String sayHello(String parameter){
  // TODO...
}

NOTE：you should implement yourMethod in SpEL of AccessLimit annotation, and make the context know where to find the method.
      you can find some references in class AccessLimitAspect.

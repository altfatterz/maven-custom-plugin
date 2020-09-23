input:

```
"KEY","EN","DE","FR","IT"
"key1","Foo-1","Bar-1","Baz-1","Biz-1"
"key2","Foo-2","Bar-2","Baz-2","Biz-2"
"key3","Foo-3",,,
```

output:

```
[
    {
    
      "key1" : "Foo-1",
      "key2" : "Foo-2",
      "key3" : "Foo-3"
    
    },
    {
    
      "key1" : "Bar-1",
      "key2" : "Bar-2",
      "key3" : "[key3]"
    
    },
    {
    
      "key1" : "Baz-1",
      "key2" : "Baz-2",
      "key3" : "[key3]"
    
    },
    {
    
      "key1" : "Biz-1",
      "key2" : "Biz-2",
      "key3" : "[key3]"
    
    }
]
```
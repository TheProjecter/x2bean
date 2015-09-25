X2Bean version 2.1 released

1. give tow interface for parsing xml from an inputstream
2. tinker up some bugs

---


X2Bean version 1.1 released
This released version supports parsing attributes. And in last version, I didn't consider the situation, there are same tag names in different path of the XML. Because I used too many "getElementsByTagName" in my codes, it made a big mistake. Now I contribute, saving these problems.
And the new version no longer relies on log4j to log.
This compressed file contains the source codes, testing codes and API documents.
Another developer foreverLi also do much for this version, so grateful for his work.


---

X2Bean
> X2Bean is a toolkit, which I proposed, for quickly parsing XML data into sample Java bean.
> Project Home: http://code.google.com/p/x2bean/

Background
> There are so many toolkits for programming with XML and Java Class. But these packages almost have a common premise: the DTD or the schema of the XML data. When we develop our programs with simple structured XML, we often don’t design the schema or the DTD file. Then these utility-packages become useless. So we need a simple toolkit for mapping the XML instance and the Java Bean.

Mapping Structure View




Version
> Version 1.0 is released on 4.12.2009.
> > Because I will deal a lot of XML data in the following work, so I write those simple codes quickly. There are a lot that I didn’t consider, and it may make mistakes when you use version 1.0. And I have found some situations in which this version toolkit will be failure. And I will revise these unthoughtful codes.

Supported Java type

> Type A: Primitive Java type;
> Type B: simple class in the “java.lang” package with construction method, that with a string as argument;
> Type C: one dimensional array of Type A, Type B, and Type D;
> Type D: simple Java beans with fields, which types are Type A, Type B, Type C, and Type D with accessible authority (public and protected).

Supported XML node type
> Now it doesn’t support attribute node. That is say, the simpler the XML structure, this toolkit works better.
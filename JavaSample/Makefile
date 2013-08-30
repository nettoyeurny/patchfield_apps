all:
	-javah -classpath bin/classes -o jni/javamodule.h \
		com.noisepages.nettoyeur.patchfield.java.JavaModule
	env NDK_MODULE_PATH=$(CURDIR)/../../patchfield ndk-build

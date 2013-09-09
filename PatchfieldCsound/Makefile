all:
	-javah -classpath bin/classes -o jni/pfcsound.h \
		com.noisepages.nettoyeur.patchfield.csound.CsoundModule
	env NDK_MODULE_PATH=$(CURDIR)/../../patchfield ndk-build

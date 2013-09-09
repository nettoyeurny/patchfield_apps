#include "pfcsound.h"

#include "audio_module.h"
#include "csound.h"
#include "utils/buffer_size_adapter.h"

#include <string.h>

static void process_func(void *context, int sample_rate, int buffer_frames,
    int input_channels, const float *input_buffer,
    int output_channels, float *output_buffer) {
  CSOUND *csound = (CSOUND *) context;
  memcpy(csoundGetSpin(csound), input_buffer,
      buffer_frames * input_channels * sizeof(float));
  csoundPerformKsmps(csound);
  memcpy(output_buffer, csoundGetSpout(csound),
      buffer_frames * output_channels * sizeof(float));
}

JNIEXPORT jlong JNICALL
Java_com_noisepages_nettoyeur_patchfield_csound_CsoundModule_configureNativeComponents
(JNIEnv *env, jobject obj, jlong handle, jlong p, jint bufferSize) {
  CSOUND *csound = (CSOUND *) p;
  return (jlong) bsa_create((void *) handle, bufferSize, csoundGetKsmps(csound),
      csoundNchnlsInput(csound), csoundNchnls(csound), process_func, csound);
}

JNIEXPORT void JNICALL
Java_com_noisepages_nettoyeur_patchfield_csound_CsoundModule_release
(JNIEnv *env, jobject obj, jlong p) {
  bsa_release((buffer_size_adapter *) p);
}

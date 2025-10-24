package com.henrique.videoeditor 

import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import android.util.Log

// Ponte entre o JavaScript e o Código Nativo FFmpeg
@CapacitorPlugin(name = "FFmpegPlugin")
class FFmpegPlugin : Plugin() {

        private val builder = FFmpegCommandBuilder() // Cria uma instância do nosso Tradutor

            @PluginMethod // Esta função é chamada pelo JavaScript: FFmpegPlugin.renderVideo()
                fun renderVideo(call: PluginCall) {
                            val videoPath = call.getString("videoPath") 
                                    val editsJson = call.getString("edits") 
                                            val outputPath = call.getString("outputPath") 

                                                    if (videoPath.isNullOrEmpty() || editsJson.isNullOrEmpty()) {
                                                                    call.reject("Caminho do vídeo ou Receita ausentes.")
                                                                                return
                                                    }
                                                            
                                                                    // 1. Pede ao Tradutor para construir o Comando FFmpeg
                                                                            val command = try {
                                                                                            builder.buildCommand(videoPath, editsJson, outputPath)
                                                                            } catch (e: Exception) {
                                                                                            Log.e("FFmpegPlugin", "Erro ao construir comando: " + e.message)
                                                                                                        call.reject("Falha ao montar o comando FFmpeg: " + e.message)
                                                                                                                    return
                                                                            }

                                                                                    Log.d("FFmpegPlugin", "Comando FFmpeg pronto: $command")
                                                                                            
                                                                                                    // 2. Executa o comando na Máquina Chef (FFmpegKit)
                                                                                                            FFmpegKit.executeAsync(command) { session ->
                                                                                                                        val returnCode = session.returnCode
                                                                                                                                    val finalOutput = session.output
                                                                                                                                    
                                                                                                                                                if (ReturnCode.isSuccess(returnCode)) {
                                                                                                                                                                    // Se der certo, avisa o JavaScript (a Receita está pronta!)
                                                                                                                                                                                    val result = JSObject()
                                                                                                                                                                                                    result.put("path", outputPath)
                                                                                                                                                                                                                    call.resolve(result)
                                                                                                                                                } else {
                                                                                                                                                                    // Se der erro, avisa o JavaScript com detalhes do erro
                                                                                                                                                                                    val errorMessage = "FFmpeg falhou com código: ${returnCode}. Output: ${finalOutput}"
                                                                                                                                                                                                    Log.e("FFmpegPlugin", errorMessage)
                                                                                                                                                                                                                    call.reject(errorMessage)
                                                                                                                                                }
                                                                                                            }
                }
}


                                                                                                                                                }
                                                                                                                                                }}
                                                                            }
                                                                            }
                                                    }
                }
}
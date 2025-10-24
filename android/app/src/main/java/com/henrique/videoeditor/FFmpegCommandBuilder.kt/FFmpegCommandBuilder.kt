package com.henrique.videoeditor 

import com.getcapacitor.JSObject

// CLASSE TRADUTORA: Transforma JSON em Linha de Comando FFmpeg
class FFmpegCommandBuilder {

        fun buildCommand(videoPath: String, editsJson: String, outputPath: String): String {
                    
                            val edits = JSObject(editsJson)
                                    val general = edits.getJSObject("general")
                                            val captionsArray = edits.getJSONArray("captions")
                                                    
                                                            val videoFilters = mutableListOf<String>()

                                                                    // 1. TRADUZINDO AJUSTES DE COR (Brightness, Contrast, Saturation)
                                                                            val brightness = general.getString("brightness").toDouble() / 100.0
                                                                                    val contrast = general.getString("contrast").toDouble() / 100.0
                                                                                            val saturate = general.getString("saturate").toDouble() / 100.0
                                                                                                    
                                                                                                            if (brightness != 1.0 || contrast != 1.0 || saturate != 1.0) {
                                                                                                                            videoFilters.add("eq=brightness=${brightness}:contrast=${contrast}:saturation=${saturate}")
                                                                                                            }

                                                                                                                    // 2. TRADUZINDO EFEITO INVERTER
                                                                                                                            val activeVFX = edits.getJSONArray("activeManualVFX").toString()
                                                                                                                                    if (activeVFX.contains("invert")) {
                                                                                                                                                    videoFilters.add("colorchannelmixer=0:1:0:0:0:0:1:0:0:0:0:1") 
                                                                                                                                    }
                                                                                                                                            
                                                                                                                                                    // 3. TRADUZINDO LEGENDAS (DrawText)
                                                                                                                                                            val drawTextCommands = mutableListOf<String>()

                                                                                                                                                                    for (i in 0 until captionsArray.length()) {
                                                                                                                                                                                    val cap = captionsArray.getJSObject(i)
                                                                                                                                                                                                val text = cap.getString("text").replace("'", "’") 
                                                                                                                                                                                                            val start = cap.getDouble("start")
                                                                                                                                                                                                                        val end = cap.getDouble("end")
                                                                                                                                                                                                                                    val color = cap.getString("color").replace("#", "0x")
                                                                                                                                                                                                                                                val animation = cap.getString("animation")
                                                                                                                                                                                                                                                            
                                                                                                                                                                                                                                                                        var drawTextCommand = "drawtext=text='${text}':fontcolor=${color}:fontsize=h/15:x=(w-text_w)/2:y=h*0.85:enable='between(t,${start},${end})'"
                                                                                                                                                                                                                                                                                    
                                                                                                                                                                                                                                                                                                if (animation == "glow") {
                                                                                                                                                                                                                                                                                                                     drawTextCommand += ":shadowcolor=black@0.7:shadowx=2:shadowy=2"
                                                                                                                                                                                                                                                                                                } else if (animation == "shake") {
                                                                                                                                                                                                                                                                                                                     drawTextCommand += ":y=h*0.85+(rand(1)*10-5)" 
                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                            
                                                                                                                                                                                                                                                                                                                        drawTextCommands.add(drawTextCommand)
                                                                                                                                                                    }
                                                                                                                                                                            
                                                                                                                                                                                    // 4. JUNÇÃO DE TODOS OS FILTROS
                                                                                                                                                                                            videoFilters.addAll(drawTextCommands)
                                                                                                                                                                                                    val finalVideoFilter = videoFilters.joinToString(separator = ",")
                                                                                                                                                                                                            
                                                                                                                                                                                                                    // 5. MONTAGEM DO COMANDO FFmpeg FINAL
                                                                                                                                                                                                                            
                                                                                                                                                                                                                                    val command = mutableListOf(
                                                                                                                                                                                                                                                    "-i", videoPath,
                                                                                                                                                                                                                                                                "-vf", finalVideoFilter,
                                                                                                                                                                                                                                                                            "-c:v", "libx264",
                                                                                                                                                                                                                                                                                        "-crf", "23",
                                                                                                                                                                                                                                                                                                    "-preset", "veryfast",
                                                                                                                                                                                                                                                                                                                "-c:a", "copy",
                                                                                                                                                                                                                                                                                                                            outputPath
                                                                                                                                                                                                                                    )

                                                                                                                                                                                                                                            return command.joinToString(" ")
        }
}


                                                                                                                                                                                                                                    )
                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                    }
                                                                                                                                    }
                                                                                                            }
        }
}
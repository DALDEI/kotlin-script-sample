package kotlinscript

import java.io.File
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvm.compat.mapLegacyDiagnosticSeverity
import kotlin.script.experimental.jvm.compat.mapLegacyScriptPosition
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext

/*
 * Script 'Template'
 * Script code is injected into the body (somewhere?)
 */

@KotlinScript(
    displayName = "MyScript",
    fileExtension = "anythingbutkts")
abstract class RatingScript(val inputMap: Map<String,Any> )



fun evaluateScript(script: String, input: Map<String, Any>) : Any? {

    // scriptCompilation.kt -- ScriptCompilationConfigurationKeys.*
    val compilation: ScriptCompilationConfiguration.Builder.() -> Unit = {
        jvm {
            // limit or extend classpath
            // "mylib.jar","another.jar", wholeClasspath = imTooLazy
            dependenciesFromCurrentContext( wholeClasspath = true)
        }
    }
    // scriptEvaluation.kt  -- ScriptEvaluationConfigurationKeys.*
    val evaluation : ScriptEvaluationConfiguration.Builder.() -> Unit = {
          constructorArgs(mapOf("Arg1" to "Value1"))

    }


    val evalResult: EvaluationResult? = BasicJvmScriptingHost().evalWithTemplate<RatingScript>(
            script = script.toScriptSource(),
            compilation = compilation,
            evaluation = evaluation
        ).resultOrNull()
     val result = (evalResult?.returnValue as ResultValue.Value).value
    return result
 //You can cast your result (depends what your script returns)
}

fun main(){

// Pay attention your args map is excesable from inside the script like 'inputMap.get() etc'
    val script = """ 
        |println("Hello from script") 
        |println( inputMap["Arg1"] )
        |println( this::class )
        |1+1
    """.trimMargin()

/*
 result returns the RatingScript object (not the expression result)
 The $$Result property is not present (not yet implemented? )

 */
    val result = evaluateScript(script,mapOf("go" to "town"))
    println( result!!::class.members.joinToString{ it.name })

}

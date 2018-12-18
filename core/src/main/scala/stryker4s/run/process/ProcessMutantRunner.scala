package stryker4s.run.process

import java.nio.file.Path

import better.files.File
import stryker4s.config.Config
import stryker4s.model._
import stryker4s.run.MutantRunner

import scala.concurrent.TimeoutException
import scala.util.{Failure, Success}

class ProcessMutantRunner(command: Command, processRunner: ProcessRunner)(implicit config: Config)
    extends MutantRunner(processRunner) {

  def runMutant(mutant: Mutant, workingDir: File, subPath: Path): MutantRunResult = {
    val id = mutant.id
    info(s"Starting test-run ${id + 1}...")
    processRunner(command, workingDir, ("ACTIVE_MUTATION", id.toString)) match {
      case Success(0)                         => Survived(mutant, subPath.toString)
      case Success(exitCode) if exitCode != 0 => Killed(mutant, subPath.toString)
      case Failure(exc: TimeoutException)     => TimedOut(exc, mutant, subPath.toString)
    }
  }
}

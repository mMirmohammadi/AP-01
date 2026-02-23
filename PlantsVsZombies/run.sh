javac -cp lib/\* $(find src -name '*.java') -d ~/.ap-project-dev/
java -cp ~/.ap-project-dev/:lib/\*:src/ --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml main.Program

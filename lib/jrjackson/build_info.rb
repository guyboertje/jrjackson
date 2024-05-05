module JrJackson
  module BuildInfo
    def self.version
      '0.4.19'
    end

    def self.release_date
      '2024-05-05'
    end

    def self.files
      git_files.concat(generated_jar_files).concat(generated_files)
    end

    def self.jackson_version
      '2.17.0'
    end

    def self.jackson_databind_version
      '2.17.0'
    end

    def self.jar_version
      '1.2.36'
    end

    private

    def self.generated_files
      Dir.glob( %w(pom.xml lib/jrjackson_jars.rb) )
    end

    def self.git_files
      `git ls-files`.split($/).reject{|s| s.start_with?("benchmarking")}
    end

    def self.generated_jar_files
      [
        "lib/com/fasterxml/jackson/core/jackson-annotations/#{jackson_version}/jackson-annotations-#{jackson_version}.jar",
        "lib/com/fasterxml/jackson/core/jackson-core/#{jackson_version}/jackson-core-#{jackson_version}.jar",
        "lib/com/fasterxml/jackson/core/jackson-databind/#{jackson_databind_version}/jackson-databind-#{jackson_databind_version}.jar",
        "lib/com/fasterxml/jackson/module/jackson-module-afterburner/#{jackson_version}/jackson-module-afterburner-#{jackson_version}.jar",
        "lib/jrjackson/jars/jrjackson-#{jar_version}.jar"
      ]
    end
  end
end

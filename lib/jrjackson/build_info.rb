module JrJackson
  module BuildInfo
    def self.version
      '0.5.2'
    end

    def self.release_date
      '2026-07-07'
    end

    def self.files
      repo_files.concat(generated_jar_files).concat(generated_files)
    end

    def self.jackson_version
      '2.21.4'
    end

    def self.jackson_annotations_version
      '2.21'
    end

    def self.jackson_databind_version
      '2.21.4'
    end

    def self.jar_version
      '1.3.0'
    end

    private

    def self.generated_files
      files = %w(pom.xml lib/jrjackson_jars.rb)
      missing = files.reject { |f| File.file?(f) }
      unless missing.empty?
        raise "jrjackson packaging error: generated file(s) missing at build time: " \
              "#{missing.join(', ')}. Run `rake vendor_jars` (or `rake compile`) to " \
              "regenerate them before building the gem."
      end
      files
    end

    def self.repo_files
      Dir["lib/**/*"].select{ |f| File.file? f } + ["README.md", "jrjackson.gemspec", ]
    end

    def self.generated_jar_files
      [
        "lib/com/fasterxml/jackson/core/jackson-annotations/#{jackson_annotations_version}/jackson-annotations-#{jackson_annotations_version}.jar",
        "lib/com/fasterxml/jackson/core/jackson-core/#{jackson_version}/jackson-core-#{jackson_version}.jar",
        "lib/com/fasterxml/jackson/core/jackson-databind/#{jackson_databind_version}/jackson-databind-#{jackson_databind_version}.jar",
        "lib/com/fasterxml/jackson/module/jackson-module-afterburner/#{jackson_version}/jackson-module-afterburner-#{jackson_version}.jar",
        "lib/jrjackson/jars/jrjackson-#{jar_version}.jar"
      ]
    end
  end
end

module JrJackson
  module BuildInfo
    def self.version
      '0.4.5'
    end

    def self.release_date
      '2018-02-17'
    end

    def self.files
      generated_files.concat(git_files)
    end

    def self.jackson_version
      '2.9.4'
    end

    def self.jar_version
      '1.2.23'
    end

    private

    def self.generated_files
      Dir.glob( %w(pom.xml lib/jrjackson_jars.rb lib/com/fasterxml/jackson/**/*.jar lib/jrjackson/jars/jrjackson-*.jar) )
    end

    def self.git_files
      `git ls-files`.split($/).reject{|s| s.start_with?("benchmarking")}
    end
  end
end

module JrJackson
  module BuildInfo
    def self.version
      '0.4.3'
    end

    def self.release_date
      '2017-09-21'
    end

    def self.files
      `git ls-files`.split($/).select{|f| f !~ /\Abenchmarking/}
    end

    def self.jackson_version
      '2.9.1'
    end

    def self.jar_version
      '1.2.21'
    end
  end
end

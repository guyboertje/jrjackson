namespace :gem do

  desc "Install the gem locally"
  task :install do
    sh "gem build jzmq.gemspec"
    sh "gem install jzmq-*.gem"
    sh "rm jzmq-*.gem"
  end

end

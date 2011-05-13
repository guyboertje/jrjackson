namespace :gem do

  desc "Install the gem locally"
  task :install do
    sh "gem build jrjackson.gemspec"
    sh "gem install jrjackson-*.gem"
    sh "rm jrjackson-*.gem"
  end

end

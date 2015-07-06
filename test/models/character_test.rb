# == Schema Information
#
# Table name: characters
#
#  id              :integer          not null, primary key
#  englishDesc     :string(255)
#  utf8hex         :string(255)
#  modmask         :integer
#  sortnumber      :integer
#  created_at      :datetime
#  updated_at      :datetime
#  key_position_id :integer
#

require 'test_helper'

class CharacterTest < ActiveSupport::TestCase
  # test "the truth" do
  #   assert true
  # end
end

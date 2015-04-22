class CreateCharacterUnicodeMappings < ActiveRecord::Migration
  def change
    create_table :character_unicode_mappings do |t|
      t.references :character, index: true
      t.references :unicode_character, index: true
      t.integer :order

      t.timestamps
    end
  end
end

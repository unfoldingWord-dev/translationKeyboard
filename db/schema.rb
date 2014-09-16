# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20140904202543) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"

  create_table "characters", force: true do |t|
    t.integer  "modmask"
    t.integer  "sortnumber"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "key_position_id"
    t.integer  "unicode_character_id"
  end

  create_table "key_positions", force: true do |t|
    t.integer  "column_index"
    t.integer  "row_index"
    t.float    "percent_width"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "keyboard_variant_id"
  end

  create_table "keyboard_types", force: true do |t|
    t.string   "name"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "keyboard_variants", force: true do |t|
    t.string   "name"
    t.integer  "keyboard_type_id"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "keyboard_id"
  end

  create_table "keyboards", force: true do |t|
    t.string   "name"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "unicode_characters", force: true do |t|
    t.string   "englishDesc"
    t.integer  "utf8hex"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

end
